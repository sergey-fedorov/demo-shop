package com.demo.shop.tests.contract.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactDirectory;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@PactConsumerTest
@PactDirectory("src/test/resources/pacts")
class ConsumerDemoShopTest {

    String validEmail = "email@gmail.com";

    @Pact(provider = "emailValidator_provider", consumer = "demoShop_consumer")
    public RequestResponsePact getEmailValidationResultSuccess(PactDslWithProvider builder) {
        return builder
                .uponReceiving("a request to validate a provided email by the validator service")
                .path("/api/email/" + validEmail)
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .booleanType("format")
                        .booleanType("disposable")
                        .booleanType("dns")
                        .booleanType("whitelist")
                        .stringType("domain")
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getEmailValidationResultSuccess", pactVersion = PactSpecVersion.V3)
    void testGetEmailValidationResultSuccess(MockServer mockServer) {
        System.setProperty("pact.writer.overwrite", "true");
        RestAssured.baseURI = mockServer.getUrl();
        RestAssured.urlEncodingEnabled = false;
        given().pathParam("email", validEmail)
                .get("/api/email/{email}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

}
