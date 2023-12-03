package com.demo.shop.service;

import com.demo.shop.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CustomerService {

    Customer get(@NotNull(message = "Customer ID is required") Long id);

    Customer create(@NotNull(message = "Customer is required") @Valid Customer customer);

}
