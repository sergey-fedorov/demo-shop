package com.demo.shop.service;


import com.demo.shop.model.OrderItem;
import com.demo.shop.model.OrderItemPK;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface OrderItemService {

    OrderItem create(@NotNull(message = "The products for order cannot be null.") @Valid OrderItem orderProduct);

    OrderItem get(OrderItemPK orderItemPK);

    void delete(OrderItemPK orderItemPK);
}
