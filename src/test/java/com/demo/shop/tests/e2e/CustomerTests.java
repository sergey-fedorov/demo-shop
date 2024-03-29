package com.demo.shop.tests.e2e;

import com.demo.shop.business.models.CustomerModel;
import com.demo.shop.business.steps.CustomerSteps;
import com.demo.shop.core.BaseTest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import static com.demo.shop.core.Validator.then_validateErrorResponse;
import static com.demo.shop.core.Validator.then_validateStatusCode;

class CustomerTests extends BaseTest {

    Faker faker = new Faker();
    CustomerSteps customerSteps = new CustomerSteps();

    @Test
    void DS_T3_newCustomerCreatedWithValidDetails(){
        CustomerModel customerReqBody = CustomerModel.getFake();

        CustomerModel customerResBody = customerSteps.when_createCustomer(customerReqBody);
        then_validateStatusCode(HttpStatus.CREATED);
        Assertions.assertEquals(customerReqBody, customerResBody);

        customerResBody = customerSteps.when_getCustomerDetails(customerResBody.getId());
        Assertions.assertEquals(customerReqBody, customerResBody);
    }

    @Test
    void DS_T4_cannotCreateCustomerWithEmptyFields(){
        CustomerModel customerReqBody = CustomerModel.getFake()
                .setFullName("");
        customerSteps.when_createCustomer(customerReqBody);
       then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Full name is required");

        customerReqBody = CustomerModel.getFake()
                .setAddress("");
        customerSteps.when_createCustomer(customerReqBody);
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Address is required");

        customerReqBody = CustomerModel.getFake()
                .setEmail("");
        customerSteps.when_createCustomer(customerReqBody);
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Email is required");
    }

    @Test
    void cannotCreateCustomerWithExistingEmail(){
        CustomerModel customerReqBody = CustomerModel.getFake();
        customerSteps.when_createCustomer(customerReqBody);
        customerSteps.when_createCustomer(customerReqBody);
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Entry already exists");
    }

    @ParameterizedTest
    @CsvSource({
            "test@gmail,Email format is not valid",
            "test@mailinator.com,Email address is disposable",
            "test@123.com,Email address is not valid"
    })
    void cannotCreateCustomerWithInvalidEmail(String email, String message){
        CustomerModel customerReqBody = CustomerModel.builder()
                .fullName(faker.name().fullName())
                .address(faker.internet().emailAddress())
                .email(email)
                .build();
        customerSteps.when_createCustomer(customerReqBody);
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, message);
    }


}
