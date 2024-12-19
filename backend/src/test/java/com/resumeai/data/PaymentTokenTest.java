package com.resumeai.data;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaymentTokenTest {

    @Test
    public void testBuilder() {
        long fourHoursLater = Instant.now().plus(4, ChronoUnit.HOURS).getEpochSecond();
        PaymentToken t = PaymentToken.builder()
            .withPaymentProvider("paypal")
            .withUidOrderId("TOKEN")
            .withTokenValue("abcdef")
            .withTokenTTL(fourHoursLater)
            .build();

        assertEquals("TOKEN", t.getUidOrderId());
        assertEquals("paypal", t.getPaymentProvider());
        assertEquals("abcdef", t.getTokenValue());
        assertEquals(fourHoursLater, t.getTokenTTL());
    }

    @Test
    void testToDdb() {
        // Arrange
        PaymentToken paymentToken = PaymentToken.builder()
            .withPaymentProvider("Stripe")
            .withUidOrderId("12345")
            .withTokenTTL(3600L)
            .withTokenValue("token-abc-123")
            .build();

        // Act
        Map<String, AttributeValue> ddbMap = paymentToken.toDdb();

        // Assert
        assertEquals(AttributeValue.builder().s("Stripe").build(), ddbMap.get("paymentProvider"));
        assertEquals(AttributeValue.builder().s("12345").build(), ddbMap.get("uidOrderId"));
        assertEquals(AttributeValue.builder().n("3600").build(), ddbMap.get("tokenTTL"));
        assertEquals(AttributeValue.builder().s("token-abc-123").build(), ddbMap.get("tokenValue"));
    }

    @Test
    void testFromDdb() {
        // Arrange
        Map<String, AttributeValue> ddbMap = Map.of(
            "paymentProvider", AttributeValue.builder().s("Stripe").build(),
            "uidOrderId", AttributeValue.builder().s("12345").build(),
            "tokenTTL", AttributeValue.builder().n("3600").build(),
            "tokenValue", AttributeValue.builder().s("token-abc-123").build()
        );

        // Act
        PaymentToken paymentToken = PaymentToken.fromDdb(ddbMap);

        // Assert
        assertNotNull(paymentToken);
        assertEquals("Stripe", paymentToken.getPaymentProvider());
        assertEquals("12345", paymentToken.getUidOrderId());
        assertEquals(3600L, paymentToken.getTokenTTL());
        assertEquals("token-abc-123", paymentToken.getTokenValue());
    }
}