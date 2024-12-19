package com.resumeai.data;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserTest {

    @Test
    public void testUser() {
        User user = User.builder()
            .withUid("uid")
            .withDisplayName("Just Name")
            .withEmail("a@example.com")
            .withEmailVerified(true)
            .build();

        assertEquals("uid", user.getUid());
        assertEquals("Just Name", user.getDisplayName());
        assertEquals("a@example.com", user.getEmail());
        assertEquals(true, user.getEmailVerified());
    }

    @Test
    public void testObjectMapper() throws JsonProcessingException {
        String json = """
            {
                "uid": "123",
                "displayName": "Doe",
                "email": "doe@example.com",
                "emailVerified": true
            }
            """;

        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(json, User.class);

        assertEquals("123", user.getUid());
        assertEquals("Doe", user.getDisplayName());
        assertEquals("doe@example.com", user.getEmail());
        assertEquals(true, user.getEmailVerified());
        assertNull(user.getPhotoUrl());
    }

    @Test
    public void testToBuilder() {
        String instant = Instant.now().toString();
        User user = User.builder()
            .withUid("uid")
            .withDisplayName("Just Name")
            .withEmail("a@example.com")
            .withEmailVerified(true)
            .build();

        User newUser = user.toBuilder()
            .withLastLogin(instant)
            .build();

        assertEquals("uid", newUser.getUid());
        assertEquals("Just Name", newUser.getDisplayName());
        assertEquals("a@example.com", newUser.getEmail());
        assertEquals(true, newUser.getEmailVerified());
        assertEquals(instant, newUser.getLastLogin());
    }

    @Test
    void testToDdb() {
        User user = User.builder()
                .withUid("12345")
                .withDisplayName("John Doe")
                .withEmail("johndoe@example.com")
                .withPhotoUrl("http://example.com/photo.jpg")
                .withEmailVerified(true)
                .withAvailableTokens(10)
                .withLastLogin("2024-12-01T10:00:00Z")
                .build();

        Map<String, AttributeValue> ddbMap = user.toDdb();

        assertEquals("12345", ddbMap.get("uid").s());
        assertEquals("John Doe", ddbMap.get("displayName").s());
        assertEquals("johndoe@example.com", ddbMap.get("email").s());
        assertEquals("http://example.com/photo.jpg", ddbMap.get("photoUrl").s());
        assertTrue(ddbMap.get("emailVerified").bool());
        assertEquals("10", ddbMap.get("availableTokens").n());
        assertEquals("2024-12-01T10:00:00Z", ddbMap.get("lastLogin").s());
    }

    @Test
    void testToDdbWithMissingOptionalFields() {
        User user = User.builder()
                .withUid("12345")
                .withDisplayName("John Doe")
                .withEmail("johndoe@example.com")
                .build();

        Map<String, AttributeValue> ddbMap = user.toDdb();

        assertEquals("12345", ddbMap.get("uid").s());
        assertEquals("John Doe", ddbMap.get("displayName").s());
        assertEquals("johndoe@example.com", ddbMap.get("email").s());
        assertFalse(ddbMap.containsKey("photoUrl"));
        assertFalse(ddbMap.containsKey("emailVerified"));
        assertFalse(ddbMap.containsKey("availableTokens"));
        assertFalse(ddbMap.containsKey("lastLogin"));
    }

    @Test
    void testFromDdb() {
        Map<String, AttributeValue> ddbMap = Map.of(
                "uid", AttributeValue.builder().s("12345").build(),
                "displayName", AttributeValue.builder().s("John Doe").build(),
                "email", AttributeValue.builder().s("johndoe@example.com").build(),
                "photoUrl", AttributeValue.builder().s("http://example.com/photo.jpg").build(),
                "emailVerified", AttributeValue.builder().bool(true).build(),
                "availableTokens", AttributeValue.builder().n("10").build(),
                "lastLogin", AttributeValue.builder().s("2024-12-01T10:00:00Z").build()
        );

        User user = User.fromDdb(ddbMap);

        assertEquals("12345", user.getUid());
        assertEquals("John Doe", user.getDisplayName());
        assertEquals("johndoe@example.com", user.getEmail());
        assertEquals("http://example.com/photo.jpg", user.getPhotoUrl());
        assertTrue(user.getEmailVerified());
        assertEquals(10, user.getAvailableTokens());
        assertEquals("2024-12-01T10:00:00Z", user.getLastLogin());
    }

    @Test
    void testFromDdbWithMissingOptionalFields() {
        Map<String, AttributeValue> ddbMap = Map.of(
                "uid", AttributeValue.builder().s("12345").build(),
                "displayName", AttributeValue.builder().s("John Doe").build(),
                "email", AttributeValue.builder().s("johndoe@example.com").build()
        );

        User user = User.fromDdb(ddbMap);

        assertEquals("12345", user.getUid());
        assertEquals("John Doe", user.getDisplayName());
        assertEquals("johndoe@example.com", user.getEmail());
        assertNull(user.getPhotoUrl());
        assertNull(user.getEmailVerified());
        assertNull(user.getAvailableTokens());
        assertNull(user.getLastLogin());
    }
}