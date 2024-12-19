package com.resumeai.data;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.NoSuchElementException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class PaymentsStore {

    private final DynamoDbClient ddb;
    private final String tableName;
    private static final String TOKEN_SORT_VALUE = "TOKEN";
    private static final String PRODUCT_PRIMARY_VALUE = "AvailableProduct";
    private static final String PARTITION_KEY = "paymentProvider";
    private static final String SORT_KEY = "uidOrderId";
    private static final int BATCH_READ_SIZE = 100;
    private static final int MAX_RETRIES = 3;

    private static final Logger logger = LoggerFactory.getLogger(PaymentsStore.class);


    @Inject
    public PaymentsStore(DynamoDbClient dynamoDbClient, String tableName) {
        this.ddb = dynamoDbClient;
        this.tableName = tableName;
    }

    public String getAccessToken(String paymentProvider) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(PARTITION_KEY, AttributeValue.builder().s(paymentProvider).build());
        key.put(SORT_KEY, AttributeValue.builder().s(TOKEN_SORT_VALUE).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build();
        
        GetItemResponse getItemResponse = ddb.getItem(getItemRequest);

        if (getItemResponse.hasItem()) {
            PaymentToken paymentToken = PaymentToken.fromDdb(getItemResponse.item());
            return paymentToken.getTokenValue();
        }
        throw new RuntimeException("Couldn't get Access Token");
    }

    public void writeAccessToken(String paymentProvider, String accessToken) {
        long ttl = Instant.now().plus(6, ChronoUnit.HOURS).getEpochSecond();
        PaymentToken paymentToken = PaymentToken.builder()
            .withTokenTTL(ttl)
            .withPaymentProvider(paymentProvider)
            .withTokenValue(accessToken)
            .withUidOrderId(TOKEN_SORT_VALUE)
            .build();
        
        Map<String, AttributeValue> item = paymentToken.toDdb();
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();
        ddb.putItem(request);
    }

    public Product getProduct(String productId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(PARTITION_KEY, AttributeValue.builder().s(PRODUCT_PRIMARY_VALUE).build());
        key.put(SORT_KEY, AttributeValue.builder().s(productId).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build();
        
        GetItemResponse getItemResponse = ddb.getItem(getItemRequest);
        if (getItemResponse.hasItem()) {
            return Product.fromDdb(getItemResponse.item());
        }
        throw new RuntimeException("Could not find product with id " + productId);
    }

    public Map<String, Product> getProducts(List<String> productIds) {
        if (productIds.size() > BATCH_READ_SIZE) {
            logger.error("Number of products on batch read too large: {}", productIds.size());
            throw new RuntimeException("Too many products in cart");
        }

        KeysAndAttributes keysAndAttributes = KeysAndAttributes.builder()
            .keys(
                productIds.stream()
                    .map(productId -> {
                        Map<String, AttributeValue> key = new HashMap<>();
                        key.put(PARTITION_KEY, AttributeValue.builder().s(PRODUCT_PRIMARY_VALUE).build());
                        key.put(SORT_KEY, AttributeValue.builder().s(productId).build());
                        return key;
                    })
                    .collect(Collectors.toList())
            )
            .build();

        List<Product> products = new ArrayList<>();
        Map<String, KeysAndAttributes> unprocessedItems = Map.of(tableName, keysAndAttributes);
        logger.info("Batch read for productIds: {}", productIds);
        for (int retries = 0; retries < MAX_RETRIES; retries++) {
            
            BatchGetItemRequest batchGetItemRequest = BatchGetItemRequest.builder()
                .requestItems(unprocessedItems)
                .build();
            BatchGetItemResponse batchGetItemResponse = ddb.batchGetItem(batchGetItemRequest);

            products.addAll(batchGetItemResponse.responses().get(tableName)
                .stream()
                .map(Product::fromDdb)
                .collect(Collectors.toList()));
            
            unprocessedItems = batchGetItemResponse.unprocessedKeys();
            if (unprocessedItems.isEmpty()) {
                return products.stream()
                    .collect(Collectors.toMap(Product::getUidOrderId, product -> product));
            }
        }
        logger.error("Failed to read some or all items in batch!");
        throw new RuntimeException("Failed product lookup!");
    }

    public void writeTransaction(Transaction transaction) {
        Map<String, AttributeValue> item = transaction.toDdb();
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();
        ddb.putItem(request);
    }

    public Transaction readTransaction(String paymentProvider, String uid, String orderId) {
        String uidOrderId = uid + "#" + orderId;
        Map<String, AttributeValue> key = Map.of(
            PARTITION_KEY, AttributeValue.builder().s(paymentProvider).build(),
            SORT_KEY, AttributeValue.builder().s(uidOrderId).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build();
        
        GetItemResponse getItemResponse = ddb.getItem(getItemRequest);
        if (getItemResponse.hasItem()) {
            return Transaction.fromDdb(getItemResponse.item());
        }
        throw new RuntimeException("Could not find transaction with uidOrderId " + uidOrderId);
    }

    public void updateTransaction(TransactionUpdate transactionUpdate) {
        Map<String, AttributeValue> key = Map.of(
            PARTITION_KEY, AttributeValue.builder().s(transactionUpdate.getPaymentProvider()).build(),
            SORT_KEY, AttributeValue.builder().s(transactionUpdate.getUidOrderId()).build());

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(tableName)  
                .key(key)
                .updateExpression(transactionUpdate.getUpdateExpression()) 
                .expressionAttributeValues(transactionUpdate.getExpressionValues()) 
                .build();
        try {
            ddb.updateItem(updateItemRequest);
        } catch (Exception e) {
            logger.error("Issue with DDB Update Transaction: {}", e.getMessage());
            throw new RuntimeException("Issue with updating transaction");
        }
    }
}
