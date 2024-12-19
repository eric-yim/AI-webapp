package com.resumeai.data;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.Map;
import java.util.HashMap;

@Getter
@Builder(setterPrefix = "with", toBuilder = true)
@ToString
public class PaymentToken {
    @NonNull
    private final String paymentProvider;
    
    @NonNull
    private final String uidOrderId;

    @NonNull
    private final long tokenTTL;

    @NonNull
    private final String tokenValue;

    public Map<String, AttributeValue> toDdb() {
        Map<String, AttributeValue> ddbMap = new HashMap<>();
        ddbMap.put("paymentProvider", AttributeValue.builder().s(paymentProvider).build());
        ddbMap.put("uidOrderId", AttributeValue.builder().s(uidOrderId).build());
        ddbMap.put("tokenTTL", AttributeValue.builder().n(String.valueOf(tokenTTL)).build());
        ddbMap.put("tokenValue", AttributeValue.builder().s(tokenValue).build());
        return ddbMap;
    }

    public static PaymentToken fromDdb(Map<String, AttributeValue> ddbMap) {
        return PaymentToken.builder()
            .withPaymentProvider(ddbMap.get("paymentProvider").s())
            .withUidOrderId(ddbMap.get("uidOrderId").s())
            .withTokenTTL(Long.parseLong(ddbMap.get("tokenTTL").n()))
            .withTokenValue(ddbMap.get("tokenValue").s())
            .build();
    }

}