package com.resumeai.data;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder(setterPrefix = "with", toBuilder = true)
@ToString
public class Product {
    @NonNull
    private final String paymentProvider;

    @NonNull
    private final String uidOrderId;

    @NonNull
    private final String description;

    @NonNull
    private final String amount;

    public Map<String, AttributeValue> toDdb() {
        Map<String, AttributeValue> ddbMap = new HashMap<>();
        ddbMap.put("paymentProvider", AttributeValue.builder().s(paymentProvider).build());
        ddbMap.put("uidOrderId", AttributeValue.builder().s(uidOrderId).build());
        ddbMap.put("description", AttributeValue.builder().s(description).build());
        ddbMap.put("amount", AttributeValue.builder().s(amount).build());
        return ddbMap;
    }

    public static Product fromDdb(Map<String, AttributeValue> ddbMap) {
        return Product.builder()
            .withPaymentProvider(ddbMap.get("paymentProvider").s())
            .withUidOrderId(ddbMap.get("uidOrderId").s())
            .withDescription(ddbMap.get("description").s())
            .withAmount(ddbMap.get("amount").s())
            .build();
    }
}
