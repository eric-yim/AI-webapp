package com.resumeai;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.resumeai.data.UserStore;
import com.resumeai.data.User;
import com.resumeai.dagger.AppComponent;
import com.resumeai.dagger.DaggerAppComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import java.util.Optional;
import com.fasterxml.jackson.core.JsonProcessingException;

import static com.resumeai.ResponseUtil.successResponse;
import static com.resumeai.ResponseUtil.defaultInternalServerErrorResponse;

public class UserHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    private UserStore userStore;
        
    @Inject
    public UserHandler(UserStore userStore) {
        this.userStore = userStore;
    }

    public Map<String, Object> getRequest(User authUser, String origin) {
        User user = null;
        try {
            user = userStore.readUser(authUser.getUid());
        } catch (NoSuchElementException e) {
            user = authUser; 
        }
        if (user == null) {
            return defaultInternalServerErrorResponse(origin);
        }
        Integer availableTokens = Optional.ofNullable(user.getAvailableTokens()).orElse(0);
        user = user.toBuilder()
            .withLastLogin(Instant.now().toString())
            .withAvailableTokens(availableTokens)
            .build();

        try {
            userStore.writeUser(user);
        } catch (Exception e) {
            logger.error("Failed to write user:" + e.getMessage());
            return defaultInternalServerErrorResponse(origin);
        }

        logger.info("Succesfully wrote user to ddb, uid: " + user.getUid());

        Map<String, Object> response = successResponse(Map.of(
                "displayName", user.getDisplayName(),
                "credits", Integer.toString(availableTokens)
            ), origin);
 
        
        logger.info("Response: " + response);
        return response;

    }
}