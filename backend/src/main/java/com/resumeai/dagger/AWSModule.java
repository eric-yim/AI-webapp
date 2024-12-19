package com.resumeai.dagger;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import javax.inject.Named;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import com.resumeai.data.UserStore;
import com.resumeai.data.PaymentsStore;
import com.resumeai.external.PaypalAuthenticator;
import com.resumeai.external.FirebaseAuthenticator;

@Module
public class AWSModule {

    private static final String DEFAULT_REGION = "us-west-2";

    @Provides
    @Singleton
    public Region provideRegion() {
        String regionEnv = System.getenv("AWS_REGION");
        if (regionEnv == null || regionEnv.isEmpty()) {
            return Region.of(DEFAULT_REGION);
        }
        return Region.of(regionEnv);
    }

    @Provides
    @Singleton
    public BedrockRuntimeClient provideBedrockClient(Region region) {
        return BedrockRuntimeClient.builder()
                .region(region) 
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Provides
    @Named("UserTable")
    public String provideUserTableName() {
        String userTable = System.getenv("USER_TABLE");
        if (userTable == null || userTable.isEmpty()) {
            throw new IllegalStateException("Environment variable USER_TABLE is not set or is empty.");
        }
        return userTable;
    }

    @Provides
    @Named("ProductTable")
    public String provideProductTableName() {
        String productTable = System.getenv("PRODUCT_TABLE");
        if (productTable == null || productTable.isEmpty()) {
            throw new IllegalStateException("Environment variable PRODUCT_TABLE is not set or is empty.");
        }
        return productTable;
    }

    @Provides
    @Named("PaypalClientId")
    public String providePaypalClientId() {
        String clientId = System.getenv("PAYPAL_APIKEY");
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalStateException("Environment variable PAYPAL_APIKEY is not set or is empty.");
        }
        return clientId;
    }

    @Provides
    @Named("PaypalSecret")
    public String providePaypalSecret() {
        String secret = System.getenv("PAYPAL_SECRET");
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("Environment variable PAYPAL_SECRET is not set or is empty.");
        }
        return secret;
    }

    @Provides
    @Singleton
    public DynamoDbClient provideDynamoDbClient(Region region) {
        return DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Provides
    @Singleton
    public UserStore userStore(DynamoDbClient dynamoDbClient, @Named("UserTable") String userTableName) {
        return new UserStore(dynamoDbClient, userTableName);
    }

    @Provides
    @Singleton
    public PaymentsStore paymentsStore(DynamoDbClient dynamoDbClient, @Named("ProductTable") String tableName) {
        return new PaymentsStore(dynamoDbClient, tableName);
    }

    @Provides
    @Singleton
    public PaypalAuthenticator paypalAuthenticator(@Named("PaypalClientId") String clientId,
        @Named("PaypalSecret") String secret) {
            return new PaypalAuthenticator(clientId, secret);
    }

    @Provides
    @Named("firebaseJson")
    public String firebaseJson() {
        return "firebase-adminsdk.json";
    }
}
