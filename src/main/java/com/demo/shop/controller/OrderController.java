package com.demo.shop.controller;


import com.demo.shop.dto.OrderItemDto;
import com.demo.shop.exception.BadRequestException;
import com.demo.shop.exception.ResourceNotFoundException;
import com.demo.shop.model.Customer;
import com.demo.shop.model.Order;
import com.demo.shop.model.OrderItem;
import com.demo.shop.model.OrderStatus;
import com.demo.shop.service.CustomerService;
import com.demo.shop.service.OrderItemService;
import com.demo.shop.service.OrderService;
import com.demo.shop.service.ProductService;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders-test")
public class OrderController {

    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    CustomerService customerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @NotNull Iterable<Order> list() {
        return this.orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable("id") final Long id) throws ResourceNotFoundException {
        return orderService.get(id);
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderForm form) {
        List<OrderItemDto> formDtos = form.getOrderItems();
        validateProductsExistence(formDtos);

        boolean doesDtoHaveProductDuplicates = !(formDtos.stream().map(OrderItemDto::getProductId).distinct().count() == formDtos.size());
        if(doesDtoHaveProductDuplicates)
            throw new BadRequestException("Cannot create order with duplicate product items");

        Order order = new Order();

        Customer customer = customerService.get(form.getCustomerId());
        order.setCustomer(customer);

        order.setStatus(OrderStatus.NEW.name());
        order = orderService.create(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDto dto : formDtos) {
            orderItems.add(
                    orderItemService.create(
                            new OrderItem(order, productService.getProduct(dto.getProductId()), dto.getQuantity())
                    )
            );
        }

        order.setOrderItems(orderItems);

       orderService.update(order);

        String uri = ServletUriComponentsBuilder
          .fromCurrentServletMapping()
          .path("/orders/{id}")
          .buildAndExpand(order.getId())
          .toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uri);

        return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
    }

    private void validateProductsExistence(List<OrderItemDto> orderItemDtos) {
        List<OrderItemDto> list = orderItemDtos
          .stream()
          .filter(op -> Objects.isNull(productService.getProduct(op.getProductId())))
          .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(list)) {
            throw new ResourceNotFoundException("Product not found");
        }
    }

    @Data
    public static class OrderForm {
        private long customerId;
        private List<OrderItemDto> orderItems;

    }
}
