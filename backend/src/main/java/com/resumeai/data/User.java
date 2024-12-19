package com.resumeai.data;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.Map;
import java.util.HashMap;

@Getter
@Builder(setterPrefix = "with", toBuilder = true)
@ToString
public class User {
    @NonNull
    private final String uid;
    
    @NonNull
    private final String displayName;

    @NonNull
    private final String email;

    private final String photoUrl;

    private final Boolean emailVerified;

    private final Integer availableTokens;

    private final String lastLogin;

    @JsonCreator
    public User(@JsonProperty("uid") String uid,
                @JsonProperty("displayName") String displayName,
                @JsonProperty("email") String email,
                @JsonProperty("photoUrl") String photoUrl,
                @JsonProperty("emailVerified") Boolean emailVerified,
                @JsonProperty("availableTokens") Integer availableTokens,
                @JsonProperty("lastLogin") String lastLogin
                ) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.emailVerified = emailVerified;
        this.availableTokens = availableTokens;
        this.lastLogin = lastLogin;
    }

    public static User fromDdb(Map<String, AttributeValue> attrs) {
        User.UserBuilder builder = User.builder()
            .withUid(attrs.get("uid").s())
            .withDisplayName(attrs.get("displayName").s())
            .withEmail(attrs.get("email").s());
        if (attrs.containsKey("photoUrl")) {
            builder.withPhotoUrl(attrs.get("photoUrl").s());
        }
        if (attrs.containsKey("emailVerified")) {
            builder.withEmailVerified(attrs.get("emailVerified").bool());
        }
        if (attrs.containsKey("availableTokens")) {
            builder.withAvailableTokens(Integer.valueOf(attrs.get("availableTokens").n()));
        }
        if (attrs.containsKey("lastLogin")) {
            builder.withLastLogin(attrs.get("lastLogin").s());
        }
        return builder.build();
    }

    public Map<String, AttributeValue> toDdb() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("uid", AttributeValue.builder().s(uid).build());
        item.put("displayName", AttributeValue.builder().s(displayName).build());
        item.put("email", AttributeValue.builder().s(email).build());
        if (photoUrl != null ) {
            item.put("photoUrl", AttributeValue.builder().s(photoUrl).build());
        }
        if (emailVerified != null ) {
            item.put("emailVerified", AttributeValue.builder().bool(emailVerified).build());
        }
        if (availableTokens != null) {
            item.put("availableTokens", AttributeValue.builder().n(Integer.toString(availableTokens)).build());
        }
        if (lastLogin != null) {
            item.put("lastLogin", AttributeValue.builder().s(lastLogin).build());
        }
        return item;
    }
}