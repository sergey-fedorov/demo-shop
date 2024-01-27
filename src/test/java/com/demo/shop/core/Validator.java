package com.demo.shop.core;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Validator {

    public static String getJsonValueAsString(String jsonField){
        return validateResponse()
                .body("$", hasKey(jsonField))
                .extract().response().jsonPath()
                .get(jsonField).toString();
    }

    private static ValidatableResponse validateResponse(){
        return HttpResponse.get()
                .then()
                .assertThat();
    }

    @Step
    public static ValidatableResponse then_validateStatusCode(HttpStatus statusCode) {
        return validateResponse()
                .statusCode(statusCode.value());
    }

    @Step
    public static ValidatableResponse then_validateSuccess() {
        return validateResponse()
                .statusCode(anyOf(is(HttpStatus.OK.value()),is(HttpStatus.CREATED.value())));
    }

    @Step
    public static void then_validateErrorResponse(HttpStatus status, String message) {
        then_validateStatusCode(status);
        assertEquals(message, getJsonValueAsString("errorMessage"), "Wrong errorMessage");
    }

}
