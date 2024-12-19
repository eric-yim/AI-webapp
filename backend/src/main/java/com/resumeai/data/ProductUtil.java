package com.resumeai.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ProductUtil {

    private ProductUtil() {}

    private static final String PRODUCT_ID = "productId";
    private static final String ORDER_ID = "orderId";

    private static final Logger logger = LoggerFactory.getLogger(ProductUtil.class);

    public static String parseProductId(String bodyString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(bodyString);
            return rootNode.get(PRODUCT_ID).asText();
        } catch (JsonProcessingException e) {
            logger.error("Could not parse product: {}", e.getMessage());
        }
        throw new RuntimeException("Expected a valid productId.");
    }

    public static List<CartItem> parseCartItems(String bodyString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(bodyString);
            JsonNode cartNode = rootNode.get("cart");

            List<CartItem> cartItems = new ArrayList<>();
            if (cartNode.isArray()) {
                for (JsonNode itemNode : cartNode) {
                    cartItems.add(
                        CartItem.builder()
                            .withUidOrderId(itemNode.get("id").asText())
                            .withQuantity(itemNode.get("quantity").asText())
                            .build());
                }
            }
            return cartItems;
        } catch (JsonProcessingException e) {
            logger.error("Could not parse cart items: {}", e.getMessage());
        }
        throw new RuntimeException("Expected a valid cart structure.");
    }

    public static String parseOrderId(String bodyString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(bodyString);
            return rootNode.get(ORDER_ID).asText();
        } catch (JsonProcessingException e) {
            logger.error("Could not parse product: {}", e.getMessage());
        }
        throw new RuntimeException("Expected a valid OrderId.");
    }

     public static CartTotalWithTokens getCartTotalWithTokens(List<CartItem> cartItems, Map<String, Product> toProducts) {
        double total = 0.0;
        int tokenTotal = 0;
        for (CartItem cartItem : cartItems) {
            int quantity = Integer.parseInt(cartItem.getQuantity());
            Product product = toProducts.get(cartItem.getUidOrderId());
            double amount = Double.parseDouble(product.getAmount());
            tokenTotal += getAITokens(product.getDescription());
            total += amount * quantity;
        }
        
        return CartTotalWithTokens.builder()
            .withAmount(String.format("%.2f", total))
            .withTokens(String.valueOf(tokenTotal))
            .build();
    }

    /**
     * Specific to Carts with expected AI Tokens
     */
    @Getter
    @Builder(setterPrefix = "with")
    public static class CartTotalWithTokens {
        @NonNull
        private String amount;
        @NonNull
        private String tokens;
    }

    /**
     * input = "{NUMBER} AI Tokens"
     */
    private static int getAITokens(String input) {
        int number = 0;
        String regex = "(\\d+) AI Tokens";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        
        if (matcher.matches()) {
            number = Integer.parseInt(matcher.group(1));
            return number;
        }
        logger.warn("Expected input NUMBER AI Tokens, but got {}. Returning 0 value", input);
        return number;
    }
}
