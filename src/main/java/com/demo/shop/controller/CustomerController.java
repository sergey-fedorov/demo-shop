package com.demo.shop.controller;

import com.demo.shop.exception.BadRequestException;
import com.demo.shop.dto.EmailValidatorDto;
import com.demo.shop.model.Customer;
import com.demo.shop.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Value("${email.validator.service}")
    private String emailValidatorService;

    @Autowired
    CustomerService customerService;

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable("id") final long id) {
        return customerService.get(id);
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer){
        String emailValidationResult = emailValidator(customer.getEmail());
        if (!emailValidationResult.isEmpty())
            throw new BadRequestException(emailValidationResult);

        customerService.create(customer);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);

    }

    public String emailValidator(String email){
        RestTemplate restTemplate = new RestTemplate();
        EmailValidatorDto validatorDto = restTemplate.getForObject(emailValidatorService + email, EmailValidatorDto.class);

        if(validatorDto == null)
            throw new BadRequestException("Email validator service error");
        if (!validatorDto.isFormat())
            return "Email format is not valid";
        else if (validatorDto.isDisposable())
            return "Email address is disposable";
        else if (!validatorDto.isDns())
            return "Email address is not valid";
        else
            return "";
    }

}
