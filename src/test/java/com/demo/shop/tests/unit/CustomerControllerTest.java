package com.demo.shop.tests.unit;

import com.demo.shop.controller.CustomerController;
import com.demo.shop.dto.EmailValidatorDto;
import com.demo.shop.exception.ResourceNotFoundException;
import com.demo.shop.model.Customer;
import com.demo.shop.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Value("${email.validator.service}")
    private String emailValidatorService;

    @InjectMocks
    CustomerController customerController;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    RestTemplate restTemplate;

    @Test
    public void shouldReturnCustomerDetails(){
        Customer customer = Customer.builder()
                .id(1L)
                .email("em@gmail.com")
                .address("adr")
                .fullName("nm")
                .build();

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        Assertions.assertEquals(customer, customerController.getCustomer(customer.getId()));
    }

    @Test
    public void shouldReturnCustomerNotFound(){
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> customerController.getCustomer(1L),
                "Not expected exception thrown"
        );
        Assertions.assertEquals("Customer not found", exception.getMessage());
    }

    /* WIP */
    @Test @Disabled
    public void shouldCreateCustomerAndReturnDetails(){
        Customer customer = Customer.builder()
                .email("em@gmail.com")
                .address("adr")
                .fullName("nm")
                .build();

        EmailValidatorDto emailValidatorDto = EmailValidatorDto.builder()
                .format(true)
                .domain("example.com")
                .disposable(false)
                .dns(true)
                .build();

        when(restTemplate.getForEntity("https://www.disify.com/api/email/example.com", EmailValidatorDto.class))
                .thenReturn(new ResponseEntity<>(emailValidatorDto, HttpStatus.OK));

        when(customerRepository.save(customer)).thenReturn(customer.setId(1L));

        Assertions.assertEquals(customer.setId(1L), customerController.createCustomer(customer).getBody());
    }

}
