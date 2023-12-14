package com.demo.shop.tests.contract;

import au.com.dius.pact.provider.junit5.HttpsTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.MalformedURLException;
import java.net.URL;

@Provider("emailValidator_provider")
@PactFolder("src/test/resources/pacts")
public class ProviderEmailValidatorServiceTest {

    @BeforeEach
    void before(PactVerificationContext context) throws MalformedURLException {
        System.setProperty("pact.verifier.disableUrlPathDecoding", "true");
        context.setTarget(HttpsTestTarget.fromUrl(new URL("https://www.disify.com:443")));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

}
