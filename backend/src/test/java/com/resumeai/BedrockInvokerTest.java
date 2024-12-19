package com.resumeai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseOutput;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BedrockInvokerTest {

    @Mock
    BedrockRuntimeClient client;

    @Mock
    ConverseResponse mockResponse;

    @Mock
    ConverseOutput mockOutput;

    private BedrockInvoker bedrockInvoker;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        bedrockInvoker = new BedrockInvoker(client);

    }

    @Test
    public void testInvoke() {
        // arrange
        Message responseMessage = Message.builder()
                .content(ContentBlock.fromText("Mocked response."))
                .role(ConversationRole.ASSISTANT)
                .build();
        when(client.converse(any(ConverseRequest.class))).thenReturn(mockResponse);
        when(mockResponse.output()).thenReturn(mockOutput);
        when(mockOutput.message()).thenReturn(responseMessage);

        // act
        String output = bedrockInvoker.invoke("HELLO");
        
        // assert
        assertEquals("Mocked response.", output);

    }
}
