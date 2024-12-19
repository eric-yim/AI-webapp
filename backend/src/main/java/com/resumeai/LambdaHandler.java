package com.resumeai;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.resumeai.external.FirebaseAuthenticator;
import com.resumeai.dagger.AppComponent;
import com.resumeai.dagger.AWSModule;
import com.resumeai.dagger.DaggerAppComponent;
import com.resumeai.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;


import static com.resumeai.ResponseUtil.defaultInternalServerErrorResponse;
import static com.resumeai.ResponseUtil.successResponse;
import static com.resumeai.ResponseUtil.invalidUser;

public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(LambdaHandler.class);

    private UserHandler userHandler;
    private ProductHandler productHandler;
    private FirebaseAuthenticator firebaseAuthenticator;
    public LambdaHandler() {
        AppComponent appComponent = DaggerAppComponent.create();
        this.userHandler = appComponent.getUserHandler();
        this.productHandler = appComponent.getProductHandler();
        this.firebaseAuthenticator = appComponent.getFirebaseAuthenticator();
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        
        logger.info("Received input: " + input);
        Map<String, String> headers = (Map<String, String>) input.get("headers");
        User user = null;
        try {
            user = authenticate(headers);
            logger.info("User found: " + user.getUid());
        } catch (Exception e) {
            logger.error("User not found. Auth token exception: " + e.getMessage());
        }

        String origin = headers.get("origin");
        if (origin == null) {
            origin = "testFromConsole";
            if (user == null) {
                user = User.builder()
                    .withUid(origin)
                    .withEmail(origin + "@example.com")
                    .withDisplayName(origin)
                    .build();
            }
        }

        if (user == null) {
            return invalidUser(origin);
        }
        
        
        String httpMethod = (String) input.get("httpMethod");
        String pathString = (String) input.get("path");
        String routeString = pathString + "#" + httpMethod;
        
        String body = (String) input.get("body");
        
        switch (routeString) {
            case "/users#POST":
                return defaultInternalServerErrorResponse(origin);
            case "/users#GET":
                return userHandler.getRequest(user, origin);  
            case "/products#POST":
                return productHandler.postRequest(user, origin, body);
            case "/products-capture#POST":
                return productHandler.capturePostRequest(user, origin, body);
            default:
                return defaultInternalServerErrorResponse(origin);
        }
    }
    private User authenticate(Map<String, String> headers) {
        firebaseAuthenticator.initializeFirebase();
        return firebaseAuthenticator.getUserFromToken(removeBearer(headers.get("Authorization")));
    }

    public static String removeBearer(String input) {
        if (input.startsWith("Bearer ")) {
            return input.replaceFirst("Bearer ", "");
        }
        return input;
    }

    
}