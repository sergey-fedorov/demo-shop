package com.demo.shop.core;

import io.restassured.response.ValidatableResponse;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Validator {

    protected static HttpRequest httpRequest;

    static {
        httpRequest = new HttpRequest();
    }


    public static String getJsonValueAsString(String jsonField){
        return validateResponse()
                .body("$", hasKey(jsonField))
                .extract().response().jsonPath()
                .get(jsonField).toString();
    }

    public static ValidatableResponse validateResponse(){
        return httpRequest.getResponse()
                .then()
                .assertThat();
    }

    public static ValidatableResponse then_validateStatusCode(HttpStatus statusCode) {
        return validateResponse()
                .statusCode(statusCode.value());
    }

    public static ValidatableResponse then_validateSuccess() {
        return validateResponse()
                .statusCode(anyOf(is(HttpStatus.OK.value()),is(HttpStatus.CREATED.value())));
    }

    public static void then_validateErrorResponse(HttpStatus status, String message) {
        then_validateStatusCode(status);
        assertEquals(message, getJsonValueAsString("errorMessage"), "Wrong errorMessage");
    }

}
