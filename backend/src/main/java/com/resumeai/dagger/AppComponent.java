package com.resumeai.dagger;

import com.resumeai.BedrockInvoker;
import com.resumeai.UserHandler;
import com.resumeai.ProductHandler;
import com.resumeai.external.FirebaseAuthenticator;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = AWSModule.class)
public interface AppComponent {
    BedrockInvoker getBedrockInvoker();

    UserHandler getUserHandler();

    ProductHandler getProductHandler();

    FirebaseAuthenticator getFirebaseAuthenticator();
}