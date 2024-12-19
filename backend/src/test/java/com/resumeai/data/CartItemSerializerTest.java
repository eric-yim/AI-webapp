package com.resumeai.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

public class CartItemSerializerTest {

    @Test
    public void testConvertToJson() {
        // Create a list of CartItems
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(CartItem.builder().withUidOrderId("a").withQuantity("1").build());

        // Convert to JSON string
        String jsonString = CartItemSerializer.convertToJson(cartItems);

        // Verify the expected JSON format
        String expectedJson = "[{\"uidOrderId\":\"a\",\"quantity\":\"1\"}]";
        assertEquals(expectedJson, jsonString);
    }

    @Test
    public void testConvertToJsonEmptyList() {
        // Test with an empty list
        List<CartItem> cartItems = new ArrayList<>();

        // Convert to JSON string
        String jsonString = CartItemSerializer.convertToJson(cartItems);

        // Verify the expected JSON format for an empty list
        String expectedJson = "[]";
        assertEquals(expectedJson, jsonString);
    }

    @Test
    public void testConvertToJsonNullList() {
        // Test with a null list
        List<CartItem> cartItems = null;

        // Convert to JSON string
        String jsonString = CartItemSerializer.convertToJson(cartItems);

        // Verify the error message returned
        String expectedJson = "Unknown";
        assertEquals(expectedJson, jsonString);
    }
}