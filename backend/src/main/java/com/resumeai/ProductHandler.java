package com.resumeai;

import com.resumeai.data.UserStore;
import com.resumeai.data.Product;
import com.resumeai.data.Transaction;
import com.resumeai.data.TransactionUpdate;
import com.resumeai.data.User;
import com.resumeai.data.CartItem;
import com.resumeai.dagger.AppComponent;
import com.resumeai.dagger.DaggerAppComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import java.util.Optional;
import java.util.List;
import org.json.JSONObject;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.resumeai.external.PaypalAuthenticator;
import com.resumeai.data.PaymentsStore;
import com.resumeai.data.PaypalCaptureResponse;
import com.resumeai.data.ProductUtil.CartTotalWithTokens;

import static com.resumeai.ResponseUtil.successResponse;
import static com.resumeai.ResponseUtil.httpResponse;
import static com.resumeai.ResponseUtil.defaultInternalServerErrorResponse;
import static com.resumeai.ResponseUtil.internalServerErrorResponse;
import static com.resumeai.ResponseUtil.otherResponse;
import static com.resumeai.data.ProductUtil.parseCartItems;
import static com.resumeai.data.ProductUtil.parseOrderId;
import static com.resumeai.data.ProductUtil.getCartTotalWithTokens;
import static com.resumeai.data.CartItemSerializer.convertToJson;

public class ProductHandler {

    private PaypalAuthenticator paypalAuthenticator;
    private PaymentsStore paymentsStore;
    private UserStore userStore;
    private static final Logger logger = LoggerFactory.getLogger(ProductHandler.class);
    private static final String PAYMENT_PROVIDER = "paypal";
        
    @Inject
    public ProductHandler(PaypalAuthenticator paypalAuthenticator, PaymentsStore paymentsStore, UserStore userStore) {
        this.userStore = userStore;
        this.paymentsStore = paymentsStore;
        this.paypalAuthenticator = paypalAuthenticator;
    }

    private String getAccessToken(User authUser) {
        String accessToken;
        try {
            accessToken = paymentsStore.getAccessToken(PAYMENT_PROVIDER);
        } catch (Exception e) {
            logger.info("Unable to get Access Token from DDB: {}", e.getMessage());
            try {
                logger.info("Getting token from paypal API");
                accessToken = paypalAuthenticator.getAccessToken();
                try {
                    paymentsStore.writeAccessToken(PAYMENT_PROVIDER, accessToken);
                } catch (Exception ex) {
                    logger.error("Could not store token: {}", ex.getMessage());
                }
                
            } catch (Exception exception) {
                logger.error("Could not get token from either DDB or API: {}", exception.getMessage());
                throw new RuntimeException("Could not get access token");
            }
        }
        return accessToken;
    }

    public Map<String, Object> postRequest(User authUser, String origin, String bodyString) {
        List<CartItem> cartItems = parseCartItems(bodyString);
        String accessToken;
        try {
            accessToken = getAccessToken(authUser);
        } catch (Exception e) {
            logger.error("Unable to get  Access Token: {}", e.getMessage());
            return internalServerErrorResponse("Transaction was not completed. Try again!", origin);
        }
        
        

        Map<String, Product> toProducts;
        try {
            List<String> productIds = cartItems.stream()
                .map(CartItem::getUidOrderId)
                .distinct()
                .collect(Collectors.toList());
                
            toProducts = paymentsStore.getProducts(productIds);
        } catch (Exception e) {
            logger.error("Could not get products: {}", e.getMessage());
            return internalServerErrorResponse("Transaction was not completed. Try again!", origin);
        }

        CartTotalWithTokens cartTotal = getCartTotalWithTokens(cartItems, toProducts);
        
        Transaction transaction;
        try {
            transaction = paypalAuthenticator.createOrder(accessToken, cartTotal.getAmount(), cartTotal.getTokens(), convertToJson(cartItems), 
                authUser.getUid());
        } catch (Exception e) {
            logger.error("Could get not get transaction: {}", e.getMessage());
            return internalServerErrorResponse("Transaction was not completed. Try again!", origin);
        }
        
        try {
            paymentsStore.writeTransaction(transaction);
        } catch (Exception e) {
            logger.error("Missed transaction write: {}", transaction);
            return internalServerErrorResponse("Transaction was not completed. Try again!", origin);
        }
        return successResponse(Map.of(
            "approveLink", transaction.getApproveLink(),
            "orderId", transaction.getOrderId()), origin);

    }

   


    public Map<String, Object> capturePostRequest(User authUser, String origin, String bodyString) {
        String orderId = parseOrderId(bodyString);

        Integer addTokens;
        try {
            addTokens = getTokenCount(authUser.getUid(), orderId);
        } catch (Exception e) {
            logger.error("Issue retrieving tokens: {}", e.getMessage());
            throw new RuntimeException("Could not get token count from OrderId");
        }

        Integer totalTokens;
        try {
            totalTokens = addTokensToUser(addTokens, authUser);
        } catch (Exception e) {
            logger.error("Issue updating user with more tokens. Should be added: {}", addTokens);
            throw new RuntimeException("Missed add tokens, payment not captured");
        }

        String accessToken;
        try {
            accessToken = getAccessToken(authUser);
        } catch (Exception e) {
            logger.error("Unable to get  Access Token: {}", e.getMessage());
            return internalServerErrorResponse("TransactionUpdate was not completed. Try again!", origin);
        }

        PaypalCaptureResponse captureResponse;
        try {
            captureResponse = paypalAuthenticator.captureOrder(accessToken, orderId, authUser.getUid());
        } catch (Exception e) {
            logger.error("Could not captureOrder with Paypal: {}", e.getMessage());
            throw new RuntimeException("Could not capture order with paypal");
        }
        
        if (!captureResponse.hasError()) {
            String httpBodyString = captureResponse.getPassthrough();
            httpBodyString = prependTotalTokens(httpBodyString, totalTokens);
            TransactionUpdate transactionUpdate = captureResponse.getTransactionUpdate();
            try {
                paymentsStore.updateTransaction(transactionUpdate);
            } catch (Exception e) {
                logger.error("Missed transaction update but order was captured!: {}", transactionUpdate);
            }
            logger.info("Response 200, body: {}", httpBodyString);
            return httpResponse(200, httpBodyString, origin);
        }
        return otherResponse(captureResponse.getStatusCode(), captureResponse.getBody(), origin);
    }

    private Integer getTokenCount(String uid, String orderId) {
        Transaction transaction = paymentsStore.readTransaction(PAYMENT_PROVIDER, uid, orderId);
        return Integer.parseInt(transaction.getTokens());
    }

    private Integer addTokensToUser(Integer addTokens, User authUser) {
        User userFromDdb = userStore.readUser(authUser.getUid());

        Integer tokens = Optional.ofNullable(userFromDdb.getAvailableTokens()).orElse(0) + addTokens;
        User newUser = authUser.toBuilder()
            .withAvailableTokens(tokens)
            .withLastLogin(Instant.now().toString())
            .build();
        userStore.writeUser(newUser);
        return tokens;
    }

    private String prependTotalTokens(String bodyString, Integer totalTokens) {
        JSONObject jsonObject = new JSONObject(bodyString);
        jsonObject.put("credits", String.valueOf(totalTokens));
        return jsonObject.toString();
    }
}
