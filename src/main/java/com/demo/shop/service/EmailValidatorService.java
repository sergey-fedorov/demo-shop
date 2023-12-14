package com.demo.shop.service;

import com.demo.shop.dto.EmailValidatorDto;
import com.demo.shop.exception.EmailValidatorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class EmailValidatorService {

    @Value("${email.validator.service}")
    private String emailValidatorService;

    public EmailValidatorDto request(String email){
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(1500))
                .setReadTimeout(Duration.ofMillis(1500))
                .build();

        ResponseEntity<EmailValidatorDto> validationResponse;

        try {
            validationResponse = restTemplate.getForEntity(emailValidatorService + "/api/email/" + email, EmailValidatorDto.class);
        } catch (HttpServerErrorException httpException) {
            throw new EmailValidatorException("Email validation request failed: " + httpException.getMessage());
        } catch (Exception e) {
            throw new EmailValidatorException(e.getMessage());
        }
        return validationResponse.getBody();
    }

}
