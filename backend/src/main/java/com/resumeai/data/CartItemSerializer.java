package com.resumeai.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CartItemSerializer {

    private static final String DEFAULT = "Unknown";
    private static final Logger logger = LoggerFactory.getLogger(CartItemSerializer.class);

    public static String convertToJson(List<CartItem> cartItems) {
        if ( cartItems == null ) {
            return DEFAULT;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(cartItems);
        } catch (Exception e) {
            logger.error("Error serializing CartItem list: {}", cartItems);
            return DEFAULT;
        }
    }
}
