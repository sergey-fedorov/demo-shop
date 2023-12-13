package com.demo.shop.service;

import com.demo.shop.dto.EmailValidatorDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailValidatorService {

    @Value("${email.validator.service}")
    private String emailValidatorService;

    public EmailValidatorDto request(String email){
        return new RestTemplate().getForObject(emailValidatorService + email, EmailValidatorDto.class);
    }

}
