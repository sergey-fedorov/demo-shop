package com.demo.shop.service;

import com.demo.shop.exception.ResourceNotFoundException;
import com.demo.shop.model.Customer;
import com.demo.shop.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Customer get(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @Override
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }
}
