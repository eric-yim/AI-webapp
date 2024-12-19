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
public class TransactionUpdate {
    @NonNull
    private String paymentProvider;
    
    @NonNull
    private String uidOrderId;

    @NonNull
    private String paymentStatus;

    @NonNull
    private String captureTimestring;

    @NonNull
    private String capturedAmountAndCurrency;

    private String payerEmail;


    public Map<String, AttributeValue> toDdb() {
        Map<String, AttributeValue> ddbMap = new HashMap<>();
        ddbMap.put("paymentProvider", AttributeValue.builder().s(paymentProvider).build());
        ddbMap.put("uidOrderId", AttributeValue.builder().s(uidOrderId).build());
        ddbMap.put("captureTimestring", AttributeValue.builder().s(captureTimestring).build());
        ddbMap.put("capturedAmountAndCurrency", AttributeValue.builder().s(capturedAmountAndCurrency).build());
        ddbMap.put("paymentStatus", AttributeValue.builder().s(paymentStatus).build());
        if (payerEmail != null) {
            ddbMap.put("payerEmail", AttributeValue.builder().s(payerEmail).build());
        }
        return ddbMap;
    }

    public TransactionUpdate fromDdb(Map<String, AttributeValue> ddbMap) {
        TransactionUpdate.TransactionUpdateBuilder builder = TransactionUpdate.builder()
            .withPaymentProvider(ddbMap.get("paymentProvider").s())
            .withUidOrderId(ddbMap.get("uidOrderId").s())
            .withCaptureTimestring(ddbMap.get("captureTimestring").s())
            .withCapturedAmountAndCurrency(ddbMap.get("captureAmountAndCurrency").s())
            .withPaymentStatus(ddbMap.get("paymentStatus").s());
        if (ddbMap.get("payerEmail") != null) {
            builder.withPayerEmail(ddbMap.get("payerEmail").s());
        }
        return builder.build();

    }

    public Map<String, AttributeValue> getExpressionValues() {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":captureTimestring", AttributeValue.builder().s(captureTimestring).build());
        expressionValues.put(":paymentStatus", AttributeValue.builder().s(paymentStatus).build());
        expressionValues.put(":capturedAmountAndCurrency", AttributeValue.builder().s(capturedAmountAndCurrency).build());
        if (payerEmail != null) {
            expressionValues.put(":payerEmail", AttributeValue.builder().s(payerEmail).build());
        }        
        return expressionValues;
    }

    public String getUpdateExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append("SET ");
        sb.append("captureTimestring = :captureTimestring");
        sb.append(", paymentStatus = :paymentStatus");
        sb.append(", capturedAmountAndCurrency = :capturedAmountAndCurrency");
        if (payerEmail != null) {
            sb.append(", payerEmail = :payerEmail");
        }
        return sb.toString();
    }
    
}
