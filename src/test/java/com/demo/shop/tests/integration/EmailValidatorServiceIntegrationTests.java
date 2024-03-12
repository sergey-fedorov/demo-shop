package com.demo.shop.tests.integration;

import com.demo.shop.dto.EmailValidatorDto;
import com.demo.shop.exception.EmailValidatorException;
import com.demo.shop.service.EmailValidatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8181)
@Testcontainers
class EmailValidatorServiceIntegrationTests {

    /* How to run without DB connection, like CustomerController?
    Using @WebMvcTest(CustomerController.class) triggers error with context */

    @Autowired
    EmailValidatorService emailValidatorService;

    static String emailValidatorServiceMock = "http://localhost:8181";
    ObjectMapper mapper = new ObjectMapper();

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.1.0");

    @DynamicPropertySource
    static void dynamicConfiguration(DynamicPropertyRegistry registry) {
        registry.add("email.validator.service", () -> emailValidatorServiceMock);
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    void shouldReturnValidationResponseSuccess() throws Exception {
        EmailValidatorDto expectedRes = EmailValidatorDto.builder()
                .format(true)
                .domain("example.com")
                .disposable(false)
                .dns(true)
                .build();

        stubFor(get(urlMatching("/api/email/.*"))
                .willReturn(ok()
                        .withHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(expectedRes))
                )
        );
        Assertions.assertEquals(expectedRes, emailValidatorService.request("email@example.com"));
    }

    @Test
    void shouldReturnValidationResponseWrongFormat() throws Exception {
        EmailValidatorDto expectedRes = EmailValidatorDto.builder()
                .format(false)
                .build();

        stubFor(get(urlMatching("/api/email/.*"))
                .willReturn(ok()
                        .withHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(expectedRes))
                )
        );
        Assertions.assertEquals(expectedRes, emailValidatorService.request("@example.com"));
    }

    @Test
    void shouldReturnValidationResponseDisposable() throws Exception {
        EmailValidatorDto expectedRes = EmailValidatorDto.builder()
                .format(true)
                .domain("mailinator.com")
                .disposable(true)
                .build();

        stubFor(get(urlMatching("/api/email/.*"))
                .willReturn(ok()
                        .withHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(expectedRes))
                )
        );
        Assertions.assertEquals(expectedRes, emailValidatorService.request("email@mailinator.com"));
    }

    @Test
    void shouldReturnValidationResponseWrongDns() throws Exception {
        EmailValidatorDto expectedRes = EmailValidatorDto.builder()
                .format(true)
                .domain("123.com")
                .disposable(false)
                .dns(false)
                .build();

        stubFor(get(urlMatching("/api/email/.*"))
                .willReturn(ok()
                        .withHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(mapper.writeValueAsString(expectedRes))
                )
        );
        Assertions.assertEquals(expectedRes, emailValidatorService.request("email@123.com"));
    }

    @Test
    void shouldThrowEmailValidatorServiceException_Not2xx() throws Exception {
        stubFor(get(urlMatching("/api/email/.*"))
                .willReturn(serverError()
                )
        );
        Exception exception = Assertions.assertThrows(
                EmailValidatorException.class, () -> emailValidatorService.request("any@any.com"));
        Assertions.assertTrue(exception.getMessage().contains("Email validation request failed: 500 Server Error"));
    }

    @Test
    void shouldThrowEmailValidatorServiceTimeoutException() throws Exception {
        stubFor(get(urlMatching("/api/email/.*"))
                .willReturn(ok()
                        .withFixedDelay(3500)
                )
        );
        Exception exception = Assertions.assertThrows(
                EmailValidatorException.class, () -> emailValidatorService.request("any@any.com"));
        Assertions.assertTrue(exception.getMessage().contains("Read timed out"));
    }



}
