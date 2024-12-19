package com.resumeai.data;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransactionTest {

    @Test
    public void testUser() {
        Transaction t = Transaction.builder()
            .withPaymentProvider("paypal")
            .withUidOrderId("uid#abcdef")
            .withProductDetails("[{\"id\":\"a\",\"quantity\":\"1\"}]")
            .withUid("uid")
            .withOrderId("abcdef")
            .withTimestring("01/01/01T00:00:00")
            .withAmount("10.00")
            .withPaymentStatus("COMPLETE")
            .withSelfLink("https://")
            .withApproveLink("https")
            .withCaptureLink("https")
            .withTokens("10")
            .build();

        assertEquals("uid", t.getUid());
        assertEquals("paypal", t.getPaymentProvider());
        assertEquals("[{\"id\":\"a\",\"quantity\":\"1\"}]", t.getProductDetails());
        assertEquals("abcdef", t.getOrderId());
        assertEquals("uid#abcdef", t.getUidOrderId());
        assertEquals("10.00", t.getAmount());
        assertEquals("COMPLETE", t.getPaymentStatus());
        assertEquals("https://", t.getSelfLink());
        assertEquals("https", t.getApproveLink());
        assertEquals("https", t.getCaptureLink());
        assertEquals("10", t.getTokens());
    }

    @Test
    public void testToDdb() {
        // Create a sample Transaction object
        Transaction transaction = Transaction.builder()
            .withPaymentProvider("PayPal")
            .withUidOrderId("12345")
            .withProductDetails("[{\"id\":\"a\",\"quantity\":\"1\"}]")
            .withOrderId("7K545906GT482980P")
            .withAmount("100.00")
            .withUid("bob")
            .withTimestring("2024-12-12 10:30:45")
            .withPaymentStatus("CREATED")
            .withSelfLink("https://api.sandbox.paypal.com/v2/checkout/orders/7K545906GT482980P")
            .withApproveLink("https://www.sandbox.paypal.com/checkoutnow?token=7K545906GT482980P")
            .withCaptureLink("https://www.sandbox.paypal.com/checkoutnow?token=7K545906GT482980P")
            .withTokens("5")
            .build();

        // Call the toDdb method
        Map<String, AttributeValue> ddbMap = transaction.toDdb();

        // Verify that the map contains the correct values
        assertNotNull(ddbMap);
        assertEquals("PayPal", ddbMap.get("paymentProvider").s());
        assertEquals("12345", ddbMap.get("uidOrderId").s());
        assertEquals("[{\"id\":\"a\",\"quantity\":\"1\"}]", ddbMap.get("productDetails").s());
        assertEquals("7K545906GT482980P", ddbMap.get("orderId").s());
        assertEquals("100.00", ddbMap.get("amount").s());
        assertEquals("bob", ddbMap.get("uid").s());
        assertEquals("2024-12-12 10:30:45", ddbMap.get("timestring").s());
        assertEquals("CREATED", ddbMap.get("paymentStatus").s());
        assertEquals("https://api.sandbox.paypal.com/v2/checkout/orders/7K545906GT482980P", ddbMap.get("selfLink").s());
        assertEquals("https://www.sandbox.paypal.com/checkoutnow?token=7K545906GT482980P", ddbMap.get("approveLink").s());
        assertEquals("https://www.sandbox.paypal.com/checkoutnow?token=7K545906GT482980P", ddbMap.get("captureLink").s());
        assertEquals("5", ddbMap.get("tokens").s());
    }

    @Test
    public void testFromDdb() {
        Map<String, AttributeValue> ddbMap = Map.ofEntries(
            Map.entry("paymentProvider", AttributeValue.builder().s("PayPal").build()),
            Map.entry("uidOrderId", AttributeValue.builder().s("12345").build()),
            Map.entry("orderId", AttributeValue.builder().s("7K545906GT482980P").build()),
            Map.entry("productDetails", AttributeValue.builder().s("[{\"id\":\"a\",\"quantity\":\"1\"}]").build()),
            Map.entry("amount", AttributeValue.builder().s("100.00").build()),
            Map.entry("uid", AttributeValue.builder().s("bob").build()),
            Map.entry("tokens", AttributeValue.builder().s("6").build()),
            Map.entry("timestring", AttributeValue.builder().s("2024-12-12 10:30:45").build()),
            Map.entry("paymentStatus", AttributeValue.builder().s("CREATED").build()),
            Map.entry("selfLink", AttributeValue.builder().s("https://api.sandbox.paypal.com/v2/checkout/orders/7K545906GT482980P").build()),
            Map.entry("approveLink", AttributeValue.builder().s("https://www.sandbox.paypal.com/checkoutnow?token=7K545906GT482980P").build()),
            Map.entry("captureLink", AttributeValue.builder().s("https://www.sandbox.paypal.com/checkoutnow?token=7K545906GT482980P").build())
        );

        Transaction transaction = Transaction.fromDdb(ddbMap);

        assertNotNull(transaction);
        assertEquals("PayPal", transaction.getPaymentProvider());
        assertEquals("12345", transaction.getUidOrderId());
        assertEquals("7K545906GT482980P", transaction.getOrderId());
        assertEquals("[{\"id\":\"a\",\"quantity\":\"1\"}]", transaction.getProductDetails());
        assertEquals("100.00", transaction.getAmount());
        assertEquals("bob", transaction.getUid());
        assertEquals("6", transaction.getTokens());
        assertEquals("2024-12-12 10:30:45", transaction.getTimestring());
        assertEquals("CREATED", transaction.getPaymentStatus());
        assertEquals("https://api.sandbox.paypal.com/v2/checkout/orders/7K545906GT482980P", transaction.getSelfLink());
        assertEquals("https://www.sandbox.paypal.com/checkoutnow?token=7K545906GT482980P", transaction.getApproveLink());
        assertEquals("https://www.sandbox.paypal.com/checkoutnow?token=7K545906GT482980P", transaction.getCaptureLink());
    }
}