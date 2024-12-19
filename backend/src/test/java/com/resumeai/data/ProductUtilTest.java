package com.resumeai.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ProductUtilTest {

    @Mock
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testParseProductIdValidBody() throws JsonProcessingException {
        String bodyString = "{\"productId\":\"12345\"}";
        String productId = ProductUtil.parseProductId(bodyString);
        assertEquals("12345", productId);
    }

    @Test
    public void testParseProductIdMissingField() {
        String bodyString = "{\"name\":\"Product Name\"}";
        assertThrows(RuntimeException.class, () -> {
            ProductUtil.parseProductId(bodyString);
        });
    }

    @Test
    public void testParseProductIdInvalidJson() {
        String bodyString = "Invalid JSON";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ProductUtil.parseProductId(bodyString);
        });
        assertEquals("Expected a valid productId.", exception.getMessage());
    }
}