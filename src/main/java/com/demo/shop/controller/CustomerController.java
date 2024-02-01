package com.demo.shop.controller;

import com.demo.shop.exception.BadRequestException;
import com.demo.shop.dto.EmailValidatorDto;
import com.demo.shop.model.Customer;
import com.demo.shop.service.CustomerService;
import com.demo.shop.service.EmailValidatorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Validated
@Tag(name = "api-customers", description = "Demo-shop")
public class CustomerController {

    @Autowired
    CustomerService customerService;
    @Autowired
    EmailValidatorService emailValidatorService;


    @GetMapping("/{customerId}")
    public Customer getCustomer(@PathVariable("customerId") @NotNull @Min(1) final Long id) {
        return customerService.get(id);
    }

    @GetMapping(value = { "" })
    public List<Customer> getCustomerList() {
        return customerService.getAll();
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer){
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
