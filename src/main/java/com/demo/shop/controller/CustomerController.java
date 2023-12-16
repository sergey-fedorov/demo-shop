package com.demo.shop.controller;

import com.demo.shop.exception.BadRequestException;
import com.demo.shop.dto.EmailValidatorDto;
import com.demo.shop.model.Customer;
import com.demo.shop.service.CustomerService;
import com.demo.shop.service.EmailValidatorService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    CustomerService customerService;
    @Autowired
    EmailValidatorService emailValidatorService;


    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable("id") @NotNull final Long id) {
        return customerService.get(id);
    }

    @GetMapping
    public List<Customer> getCustomerList() {
        return customerService.getAll();
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer){
        String email = customer.getEmail();
        if (email == null || email.trim().isEmpty())
            throw new BadRequestException("Email is required");

        EmailValidatorDto validatorDto =
                emailValidatorService.request(customer.getEmail());

        String emailValidationResult = getEmailValidationError(validatorDto);
        if (!emailValidationResult.isEmpty())
            throw new BadRequestException(emailValidationResult);

        customerService.create(customer);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    public String getEmailValidationError(EmailValidatorDto validatorDto){
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
