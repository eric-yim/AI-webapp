package com.resumeai;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.InferenceConfiguration;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BedrockInvoker {

    private final BedrockRuntimeClient client;

    private static final String CLAUDE_INSTANT = "anthropic.claude-instant-v1";

    private static final String CLAUDE_3_HAIKU = "anthropic.claude-3-haiku-20240307-v1:0";

    private static final Logger logger = LoggerFactory.getLogger(BedrockInvoker.class);

    @Inject
    public BedrockInvoker(BedrockRuntimeClient bedrockClient) {
        this.client = bedrockClient;
    }

    public String invoke(String prompt) {
        String modelId = CLAUDE_3_HAIKU;
        logger.info("Starting bedrock prompt: {}", prompt);
        
        
        

        try {
            ConverseRequest request = buildConverseRequest(modelId, prompt);
            ConverseResponse response = client.converse(request);
            logger.info("Response: {}", response);
            String responseText = response.output().message().content().get(0).text();
            
            return responseText;

        } catch (SdkClientException e) {
            logger.error("ERROR: Can't invoke '{}'. Reason: {}", modelId, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static ConverseRequest buildConverseRequest(String modelId, String prompt) {
        Message message = Message.builder()
            .content(ContentBlock.fromText(prompt))
            .role(ConversationRole.USER)
            .build();


        return ConverseRequest.builder()
                .modelId(modelId)
                .messages(message)
                .inferenceConfig(buildInferenceConfig())
                .build();
    }

    private static InferenceConfiguration buildInferenceConfig() {
        return InferenceConfiguration.builder()
                .maxTokens(512)
                .temperature(0.5F)
                .topP(0.9F)
                .build();
    }
}
