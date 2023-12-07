package com.demo.shop.controller;

import com.demo.shop.exception.BadRequestException;
import com.demo.shop.dto.EmailValidatorDto;
import com.demo.shop.exception.ResourceNotFoundException;
import com.demo.shop.model.Customer;
import com.demo.shop.repository.CustomerRepository;
import jakarta.validation.constraints.NotNull;
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
    CustomerRepository customerRepository;

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable("id") @NotNull final Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer){
        String email = customer.getEmail();
        if (email == null || email.trim().isEmpty())
            throw new BadRequestException("Email is required");

        EmailValidatorDto validatorDto =
                new RestTemplate().getForObject(emailValidatorService + customer.getEmail(), EmailValidatorDto.class);

        String emailValidationResult = emailValidator(validatorDto);
        if (!emailValidationResult.isEmpty())
            throw new BadRequestException(emailValidationResult);

        customerRepository.save(customer);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }


    public String emailValidator(EmailValidatorDto validatorDto){
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
