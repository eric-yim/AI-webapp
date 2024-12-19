package com.resumeai.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartItemTest {

    @Test
    public void testCartItemCreation() {
        CartItem cartItem = CartItem.builder()
            .withUidOrderId("bulkTokenZ")
            .withQuantity("1")
            .build();

        assertNotNull(cartItem);
        assertEquals("bulkTokenZ", cartItem.getUidOrderId());
        assertEquals("1", cartItem.getQuantity());
    }

    @Test
    public void testCartItemBuilderWithNullUidOrderId() {
        // Test that null uidOrderId throws exception
        assertThrows(NullPointerException.class, () -> {
            CartItem.builder()
                .withUidOrderId(null)
                .withQuantity("1")
                .build();
        });
    }

    @Test
    public void testCartItemBuilderWithNullQuantity() {
        // Test that null quantity throws exception
        assertThrows(NullPointerException.class, () -> {
            CartItem.builder()
                .withUidOrderId("bulkTokenZ")
                .withQuantity(null)
                .build();
        });
    }

    @Test
    public void testCartItemToString() {
        // Test that toString method produces expected result
        CartItem cartItem = CartItem.builder()
            .withUidOrderId("bulkTokenZ")
            .withQuantity("1")
            .build();

        String expectedToString = "CartItem(uidOrderId=bulkTokenZ, quantity=1)";
        assertEquals(expectedToString, cartItem.toString());
    }
}