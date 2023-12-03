package com.demo.shop.service;


import com.demo.shop.model.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface OrderService {

    @NotNull
    Iterable<Order> getAllOrders();

    Order get(Long id);

    void delete(Long id);

    Order create(@NotNull(message = "The order cannot be null.") @Valid Order order);

    void update(@NotNull(message = "The order cannot be null.") @Valid Order order);
}
