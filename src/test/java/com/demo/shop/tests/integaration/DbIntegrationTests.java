package com.demo.shop.tests.integaration;

import com.demo.shop.model.Customer;
import com.demo.shop.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class DbIntegrationTests {

    /* WIP */

    @Mock
    CustomerRepository customerRepository;

    @Test
    public void shouldSaveAndFetchPerson() {

        Customer customer = Customer.builder()
                .email("em@gmail.com")
                .address("adr")
                .fullName("nm")
                .build();

        Customer save = customerRepository.save(customer);

        Assertions.assertEquals(customer, customerRepository.findById(save.getId()).get());

    }
}
