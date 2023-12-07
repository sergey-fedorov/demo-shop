package com.demo.shop.core;

import io.restassured.response.ValidatableResponse;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasKey;

public class BaseApi {

    protected HttpRequest httpRequest;

    public BaseApi(){
        httpRequest = new HttpRequest();
    }


    public <T> T getResponseAs(Class<T> asClass){
        return httpRequest.getResponse().as(asClass);
    }

    public String getJsonValueAsString(String jsonField){
        return validateResponse()
                .body("$", hasKey(jsonField))
                .extract().response().jsonPath()
                .get(jsonField).toString();
    }

    public ValidatableResponse validateResponse(){
        return httpRequest.getResponse()
                .then()
                .assertThat();
    }

    public ValidatableResponse then_validateStatusCode(HttpStatus statusCode) {
        return validateResponse()
                .statusCode(statusCode.value());
    }

    public void then_validateErrorResponse(HttpStatus status, String message) {
        then_validateStatusCode(status);
        assertEquals(message, getJsonValueAsString("errorMessage"), "Wrong errorMessage");
    }


}
