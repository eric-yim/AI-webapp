package com.resumeai;

import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseUtil {
    
    private ResponseUtil() {}

    private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);


    public static Map<String, Object> successResponse(Map<String, String> body, String origin) {
        String bodyString;
        try {            
            bodyString = new ObjectMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            logger.error("Issue with objectMapper: " + e.getMessage());
            return defaultInternalServerErrorResponse(origin);
        }
        return httpResponse(200, bodyString, origin);

    }

    public static Map<String, Object> notFoundResponse(String origin) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", "RouteNotFound");
        errorDetails.put("error", "true");
        String bodyString = "{}";
        try {
            bodyString = new ObjectMapper().writeValueAsString(errorDetails);
        } catch (JsonProcessingException e) {
            logger.error("Could not map http response: {}", errorDetails);
        }
        return httpResponse(404, bodyString, origin);
    }

    public static Map<String, Object> internalServerErrorResponse(String errorMessage, String origin) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", errorMessage);
        errorDetails.put("error", "true");
        String bodyString = "{}";
        try {
            bodyString = new ObjectMapper().writeValueAsString(errorDetails);
        } catch (JsonProcessingException e) {
            logger.error("Could not map http response: {}", errorDetails);
        }

        return httpResponse(500, bodyString, origin);
    }

    public static Map<String, Object> defaultInternalServerErrorResponse(String origin) {
        return internalServerErrorResponse("Oops!", origin);
    }

    public static Map<String, Object> invalidUser(String origin) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", "Could not validate user!");
        errorDetails.put("error", "true");
        String bodyString = "{}";
        try {
            bodyString = new ObjectMapper().writeValueAsString(errorDetails);
        } catch (JsonProcessingException e) {
            logger.error("Could not map http response: {}", errorDetails);
        }
        return httpResponse(400, bodyString, origin);
    }

    public static Map<String, Object> otherResponse(int statusCode, Map<String, Object> body, String origin) {
        String bodyString="{}";
        try {            
            bodyString = new ObjectMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            logger.error("Issue with objectMapper: " + e.getMessage());
            return defaultInternalServerErrorResponse(origin);
        }
        return httpResponse(statusCode, bodyString, origin);
    }

    public static Map<String, Object> httpResponse(int statusCode, String bodyString, String origin) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", headers(origin));
        response.put("body", bodyString);
        return response;
    }

    private static Map<String, String> headers(String origin) {
        return Map.of("Content-Type", "application/json",
            "Access-Control-Allow-Origin", origin,
            "Access-Control-Allow-Credentials", "true");
    }

}