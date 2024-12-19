package com.resumeai.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.ZoneOffset;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PaypalParser {
    private PaypalParser () {}

    private static final String PAYPAL = "paypal";
    private static final Logger logger = LoggerFactory.getLogger(PaypalParser.class);

    public static TransactionUpdate parseSuccessResponse(String uid, String orderId, String jsonResponse) throws JsonProcessingException {
        String timestring = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)  // Optionally set the time zone
            .format(Instant.now());

        ObjectMapper objectMapper = new ObjectMapper();
        PayPalOrderResponse response = objectMapper.readValue(jsonResponse, PayPalOrderResponse.class);

        String payerEmail = null;
        String captureAmount = "";
        if (response.getPayer() != null) {
            payerEmail = response.getPayer().getEmailAddress();
        }
        if (response.getPurchaseUnits() != null) {
            List<PurchaseUnit> purchaseUnits = response.getPurchaseUnits();
            if (purchaseUnits.isEmpty()) {
                logger.error("No purchase units in order found: {}", jsonResponse);
                throw new RuntimeException("Could not find purchase info");
            } else if (purchaseUnits.size() > 1) {
                logger.error("Expected 1 purchase unit but found more: {}", jsonResponse);
            }
            PurchaseUnit purchaseUnit = purchaseUnits.get(0);
            if (purchaseUnit.getPayments() != null && purchaseUnit.getPayments().getCaptures() != null) {
                List<Capture> captures = purchaseUnit.getPayments().getCaptures();
                if (captures.isEmpty()) {
                    logger.error("No captures found: {}", jsonResponse);
                    throw new RuntimeException("Could not find capture amount");
                } else if (captures.size() > 1) {
                    logger.error("Expected 1 capture amount but found more: {}", jsonResponse);
                }
                Capture capture = captures.get(0);
                captureAmount = capture.getAmount().getValue() + " " + capture.getAmount().getCurrencyCode();
            }
        }
        TransactionUpdate.TransactionUpdateBuilder builder = TransactionUpdate.builder()
            .withPaymentProvider(PAYPAL)
            .withUidOrderId(uid + "#" + orderId)
            .withPaymentStatus(response.getStatus())
            .withCaptureTimestring(timestring)
            .withCapturedAmountAndCurrency(captureAmount);
        if (payerEmail != null) {
            builder.withPayerEmail(payerEmail);
        }
        return builder.build();
    }

    // POJO Classes for JSON Mapping
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayPalOrderResponse {
        private String id;
        private String status;
        private Payer payer;
        private List<PurchaseUnit> purchaseUnits;

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Payer getPayer() {
            return payer;
        }

        public void setPayer(Payer payer) {
            this.payer = payer;
        }

        @JsonProperty("purchase_units")
        public List<PurchaseUnit> getPurchaseUnits() {
            return purchaseUnits;
        }

        public void setPurchaseUnits(List<PurchaseUnit> purchaseUnits) {
            this.purchaseUnits = purchaseUnits;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payer {
        private String emailAddress;

        @JsonProperty("email_address")
        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PurchaseUnit {
        private String referenceId;
        private Shipping shipping;
        private Payments payments;

        @JsonProperty("reference_id")
        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }

        public Shipping getShipping() {
            return shipping;
        }

        public void setShipping(Shipping shipping) {
            this.shipping = shipping;
        }

        public Payments getPayments() {
            return payments;
        }

        public void setPayments(Payments payments) {
            this.payments = payments;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Shipping {
        private Address address;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String addressLine1;

        @JsonProperty("address_line_1")
        public String getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payments {
        private List<Capture> captures;

        public List<Capture> getCaptures() {
            return captures;
        }

        public void setCaptures(List<Capture> captures) {
            this.captures = captures;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Capture {
        private String id;
        private String status;
        private Amount amount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Amount getAmount() {
            return amount;
        }

        public void setAmount(Amount amount) {
            this.amount = amount;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        private String currencyCode;
        private String value;

        @JsonProperty("currency_code")
        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
