package com.resumeai.data;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class UserStore {

    private final DynamoDbClient ddb;
    private final String userTableName;

    private static final Logger logger = LoggerFactory.getLogger(UserStore.class);

    @Inject
    public UserStore(DynamoDbClient dynamoDbClient, String userTableName) {
        this.ddb = dynamoDbClient;
        this.userTableName = userTableName;
    }

    public User readUser(String userId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("uid", AttributeValue.builder().s(userId).build());

        GetItemRequest request = GetItemRequest.builder()
            .tableName(userTableName)
            .key(key)
            .build();
        try {
            GetItemResponse response = ddb.getItem(request);

            if (response.hasItem()) {
                return User.fromDdb(response.item());
            } else {
                logger.error("User with ID {} not found.", userId);
                throw new NoSuchElementException("User with ID " + userId + " not found.");
            }
        } catch (DynamoDbException e) {
            logger.error("Failed to read user: {}", e.getMessage());
            throw new RuntimeException("Failed to read user", e);
        }
    }

    public void writeUser(User user) {
        Map<String, AttributeValue> item = user.toDdb();
        PutItemRequest request = PutItemRequest.builder()
            .tableName(userTableName)
            .item(item)
            .build();
        try {
            ddb.putItem(request);
        } catch (DynamoDbException e) {
            logger.error("Failed to write user: {}", user);
            throw new RuntimeException("Failed to write user: " + e.getMessage(), e);
        }
    }

    

    
}
