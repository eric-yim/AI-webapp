package com.resumeai.external;

import com.resumeai.data.User;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.Collections;
import java.io.InputStream;

public class FirebaseAuthenticator {

    private String firebaseSdkJson;
    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticator.class);
    // static
    @Inject
    public FirebaseAuthenticator(@Named("firebaseJson") String firebaseSdkJson) {
        this.firebaseSdkJson = firebaseSdkJson;
    }

    public void initializeFirebase() {
        if (!FirebaseApp.getApps().isEmpty()) {
            logger.info("Firebase already initialized");
            return;
        }
        
        try {
            InputStream serviceAccount = FirebaseAuthenticator.class.getClassLoader()
                    .getResourceAsStream(firebaseSdkJson);

            if (serviceAccount == null) {
                logger.error("Could not load {}", firebaseSdkJson);
                throw new RuntimeException(firebaseSdkJson + " not found in resources");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            // Initialize the FirebaseApp
            FirebaseApp.initializeApp(options);
            logger.info("Firebase initialized successfully!");

        } catch (Exception e) {
            logger.error("Failed to initialize Firebase: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

    /**
     * Verifies the ID Token and retrieves the user associated with it.
     *
     * @param idTokenString The ID token sent from the client (e.g., from Firebase Authentication).
     * @return The UserRecord object containing user details.
     */
    private static UserRecord getUserFromFirebaseToken(String idTokenString) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idTokenString);

            String uid = decodedToken.getUid();
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);

            return userRecord;

        } catch (Exception e) {
            logger.error("Error getting user from token: {}", e.getMessage());
            throw new RuntimeException("Error verifying ID token or retrieving user", e);
        }
    }
    


    public static User getUserFromToken(String idTokenString) {

        UserRecord record = getUserFromFirebaseToken(idTokenString);
        return User.builder()
            .withUid(record.getUid())
            .withDisplayName(record.getDisplayName())
            .withEmail(record.getEmail())
            .withEmailVerified(record.isEmailVerified())
            .withPhotoUrl(record.getPhotoUrl())
            .build();
    }

}