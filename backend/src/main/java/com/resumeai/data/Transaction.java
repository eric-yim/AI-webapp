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
public class Transaction {
    @NonNull
    private final String paymentProvider;
    
    @NonNull
    private final String uidOrderId;

    @NonNull
    private final String orderId;

    @NonNull
    private final String productDetails;

    @NonNull
    private final String amount;

    @NonNull
    private final String uid;

    @NonNull
    private final String timestring;

    @NonNull
    private final String paymentStatus;

    @NonNull
    private final String selfLink;

    @NonNull
    private final String approveLink;

    @NonNull
    private final String captureLink;

    @NonNull
    private final String tokens;

    public Map<String, AttributeValue> toDdb() {
        Map<String, AttributeValue> ddbMap = new HashMap<>();
        ddbMap.put("paymentProvider", AttributeValue.builder().s(paymentProvider).build());
        ddbMap.put("uidOrderId", AttributeValue.builder().s(uidOrderId).build());
        ddbMap.put("orderId", AttributeValue.builder().s(orderId).build());
        ddbMap.put("productDetails", AttributeValue.builder().s(productDetails).build());
        ddbMap.put("amount", AttributeValue.builder().s(amount).build());
        ddbMap.put("uid", AttributeValue.builder().s(uid).build());
        ddbMap.put("timestring", AttributeValue.builder().s(timestring).build());
        ddbMap.put("paymentStatus", AttributeValue.builder().s(paymentStatus).build());
        ddbMap.put("selfLink", AttributeValue.builder().s(selfLink).build());
        ddbMap.put("approveLink", AttributeValue.builder().s(approveLink).build());
        ddbMap.put("captureLink", AttributeValue.builder().s(captureLink).build());
        ddbMap.put("tokens", AttributeValue.builder().s(tokens).build());
        return ddbMap;
    }

    public static Transaction fromDdb(Map<String, AttributeValue> ddbMap) {
        return Transaction.builder()
            .withPaymentProvider(ddbMap.get("paymentProvider").s())
            .withUidOrderId(ddbMap.get("uidOrderId").s())
            .withOrderId(ddbMap.get("orderId").s())
            .withProductDetails(ddbMap.get("productDetails").s())
            .withAmount(ddbMap.get("amount").s())
            .withUid(ddbMap.get("uid").s())
            .withTimestring(ddbMap.get("timestring").s())
            .withPaymentStatus(ddbMap.get("paymentStatus").s())
            .withSelfLink(ddbMap.get("selfLink").s())
            .withApproveLink(ddbMap.get("approveLink").s())
            .withCaptureLink(ddbMap.get("captureLink").s())
            .withTokens(ddbMap.get("tokens").s())
            .build();
    }
}