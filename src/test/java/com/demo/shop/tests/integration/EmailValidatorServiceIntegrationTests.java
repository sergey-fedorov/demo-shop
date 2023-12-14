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

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8181)
public class EmailValidatorServiceIntegrationTests {

    @Autowired
    EmailValidatorService emailValidatorService;

    static String emailValidatorServiceMock = "http://localhost:8181";
    ObjectMapper mapper = new ObjectMapper();


    @DynamicPropertySource
    static void dynamicConfiguration(DynamicPropertyRegistry registry) {
        registry.add("email.validator.service", () -> emailValidatorServiceMock);
    }

    @Test
    public void shouldReturnValidationResponseSuccess() throws Exception {
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
    public void shouldReturnValidationResponseWrongFormat() throws Exception {
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
    public void shouldReturnValidationResponseDisposable() throws Exception {
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
    public void shouldReturnValidationResponseWrongDns() throws Exception {
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
    public void shouldThrowEmailValidatorServiceException_Not2xx() throws Exception {
        stubFor(get(urlMatching("/api/email/.*"))
                .willReturn(serverError()
                )
        );
        Exception exception = Assertions.assertThrows(
                EmailValidatorException.class, () -> emailValidatorService.request("any@any.com"));
        Assertions.assertTrue(exception.getMessage().contains("Email validation request failed: 500 Server Error"));
    }

    @Test
    public void shouldThrowEmailValidatorServiceTimeoutException() throws Exception {
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
