package com.resumeai.data;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductTest {

    @Test
    public void testBuilder() {
     
        Product t = Product.builder()
            .withPaymentProvider("AvailableProduct")
            .withUidOrderId("abcdef")
            .withDescription("description")
            .withAmount("9.99")
            .build();

        assertEquals("abcdef", t.getUidOrderId());
        assertEquals("AvailableProduct", t.getPaymentProvider());
        assertEquals("description", t.getDescription());
        assertEquals("9.99", t.getAmount());
    }

    @Test
    public void testToDdb() {
        Product product = Product.builder()
            .withPaymentProvider("PayPal")
            .withUidOrderId("12345")
            .withDescription("Product Description")
            .withAmount("100.00")
            .build();

        Map<String, AttributeValue> ddbMap = product.toDdb();

        assertNotNull(ddbMap);
        assertEquals(4, ddbMap.size());
        assertEquals("PayPal", ddbMap.get("paymentProvider").s());
        assertEquals("12345", ddbMap.get("uidOrderId").s());
        assertEquals("Product Description", ddbMap.get("description").s());
        assertEquals("100.00", ddbMap.get("amount").s());
    }

    @Test
    public void testFromDdb() {
        Map<String, AttributeValue> ddbMap = Map.of(
            "paymentProvider", AttributeValue.builder().s("PayPal").build(),
            "uidOrderId", AttributeValue.builder().s("12345").build(),
            "description", AttributeValue.builder().s("Product Description").build(),
            "amount", AttributeValue.builder().s("100.00").build()
        );

        Product product = Product.fromDdb(ddbMap);

        assertNotNull(product);
        assertEquals("PayPal", product.getPaymentProvider());
        assertEquals("12345", product.getUidOrderId());
        assertEquals("Product Description", product.getDescription());
        assertEquals("100.00", product.getAmount());
    }
}