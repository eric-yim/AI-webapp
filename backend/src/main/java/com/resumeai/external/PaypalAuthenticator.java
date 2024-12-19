package com.resumeai.external;

import com.resumeai.data.Product;
import com.resumeai.data.Transaction;
import com.resumeai.data.TransactionUpdate;
import com.resumeai.data.PaypalCaptureResponse;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.ZoneOffset;
import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.time.format.DateTimeFormatter;
import static com.resumeai.data.PaypalParser.parseSuccessResponse;

public class PaypalAuthenticator {

    private final String clientId;
    private final String clientSecret;
    private static final String PAYPAL_API_URL = "https://api.sandbox.paypal.com";
    private static final String PAYPAL = "paypal";
    private static final Logger logger = LoggerFactory.getLogger(PaypalAuthenticator.class);

    @Inject
    public PaypalAuthenticator(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() throws IOException {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        logger.info("Attempting to get Access Token");
        URL url = new URL(PAYPAL_API_URL + "/v1/oauth2/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String body = "grant_type=client_credentials";
        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(body);
            writer.flush();
        }

        int responseCode = connection.getResponseCode();
        logger.info("Response code from paypal: {}", String.valueOf(responseCode));
        if (responseCode != 200) {
            throw new IOException("Failed to get access token. Response code: " + responseCode);
        }

        String response = readResponse(connection);
        logger.info("Response from paypal: {}", response);
        String accessToken = parseAccessToken(response);
        return accessToken;
    }

    private static String parseAccessToken(String response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        String accessToken = jsonNode.get("access_token").asText();
        return accessToken;
    }

    public Transaction createOrder(String accessToken, String amount, String tokens, String productDetails, String uid) throws IOException {
        URL url = new URL(PAYPAL_API_URL + "/v2/checkout/orders");
        logger.info("Attempting to createOrder");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonBody = "{\n" +
                "  \"intent\": \"CAPTURE\",\n" +
                "  \"purchase_units\": [\n" +
                "    {\n" +
                "      \"amount\": {\n" +
                "        \"currency_code\": \"USD\",\n" +
                "        \"value\": \"" + amount + "\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(jsonBody);
            writer.flush();
        }

        int responseCode = connection.getResponseCode();
        logger.info("Response code from paypal: {}", String.valueOf(responseCode));
        if (responseCode != 201) {
            throw new IOException("Failed to create order. Response code: " + responseCode);
        }

        String response = readResponse(connection);
        logger.info("Response from paypal: {}", response);
        Transaction transaction = parseOrderId(response, amount, tokens, uid, productDetails);
        logger.info("Parsed transaction : {}", transaction);
        return transaction;
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    private Transaction parseOrderId(String response, String amount, String tokens, String uid, String productDetails) throws IOException {
        String timestring = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)  // Optionally set the time zone
            .format(Instant.now());
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);

        String orderId = rootNode.get("id").asText();
        String status = rootNode.get("status").asText();
        JsonNode linksArray = rootNode.get("links");

        String selfLink = null;
        String approveLink = null;
        String captureLink = null;

        for (JsonNode linkNode : linksArray) {
            String rel = linkNode.get("rel").asText();
            if ("self".equals(rel)) {
                selfLink = linkNode.get("href").asText();
            } else if ("approve".equals(rel)) {
                approveLink = linkNode.get("href").asText();
            } else if ("capture".equals(rel)) {
                captureLink = linkNode.get("href").asText();
            }
        }
        String uidOrderId = uid + "#" + orderId;

        return Transaction.builder()
            .withPaymentProvider(PAYPAL)
            .withUidOrderId(uidOrderId)
            .withUid(uid)
            .withOrderId(orderId)
            .withProductDetails(productDetails)
            .withTimestring(timestring)
            .withAmount(amount)
            .withPaymentStatus(status)
            .withSelfLink(selfLink)
            .withApproveLink(approveLink)
            .withCaptureLink(captureLink)
            .withTokens(tokens)
            .build();
    }

    public PaypalCaptureResponse captureOrder(String accessToken, String orderId, String uid) throws IOException {
        logger.info("Attempting to captureOrder for orderId: {}", orderId);

        URL url = new URL(PAYPAL_API_URL + "/v2/checkout/orders/" + orderId + "/capture");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Authorization", "Bearer " + accessToken);
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setDoOutput(true);

        InputStream responseStream;
        int statusCode = httpConn.getResponseCode();
        if (statusCode / 100 == 2) {
            responseStream = httpConn.getInputStream();
            Scanner s = new Scanner(responseStream).useDelimiter("\\A");
		    String response = s.hasNext() ? s.next() : "";
            logger.info("Capture response: {}", response);
            TransactionUpdate transactionUpdate = parseSuccessResponse(uid, orderId, response);
            return new PaypalCaptureResponse(transactionUpdate, response, statusCode);
        } 
        responseStream = httpConn.getErrorStream();
        Scanner es = new Scanner(responseStream).useDelimiter("\\A");
        String eresponse = es.hasNext() ? es.next() : "";
        logger.info("Capture response: {}", eresponse);
        Map<String, Object> body;
        if (statusCode == 422) {
            body = Map.of("details", List.of(Map.of("issue", "INSTRUMENT_DECLINED",
                "description", "The instrument was declined. Try a different payment method.")));
        } else {
            body = Map.of("details", List.of(Map.of("issue", "OTHER_ERROR",
                "description", "Could not recover processing. Try again.")));
        }
        
        return new PaypalCaptureResponse(body, statusCode);
    }
}
