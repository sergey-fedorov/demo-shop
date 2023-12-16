package com.demo.shop.controller;


import com.demo.shop.dto.OrderRequestDto;
import com.demo.shop.dto.OrderItemDto;
import com.demo.shop.exception.BadRequestException;
import com.demo.shop.exception.PaymentServiceException;
import com.demo.shop.exception.ResourceNotFoundException;
import com.demo.shop.model.Customer;
import com.demo.shop.model.Order;
import com.demo.shop.model.OrderItem;
import com.demo.shop.model.OrderStatus;
import com.demo.shop.service.CustomerService;
import com.demo.shop.service.OrderItemService;
import com.demo.shop.service.OrderService;
import com.demo.shop.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@NoArgsConstructor
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
    public @NotNull List<Order> list(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return orderService.getAllOrders()
                    .stream().filter(
                            order -> order.getStatus().equalsIgnoreCase(status)
                    ).toList();
        }
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable("id") final Long id) throws ResourceNotFoundException {
        return orderService.get(id);
    }

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody OrderRequestDto form) {
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

       return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/status")
    public ResponseEntity<Order> updateOrderStatus(@Valid @RequestBody OrderRequest orderRequest){
        Order order = orderService.get(orderRequest.getOrderId());
        if (!order.getStatus().equals(OrderStatus.PAYMENT_SUCCEEDED.name()))
            throw new BadRequestException("Cannot update status for a not paid order");

        order.setStatus(OrderStatus.DELIVERED.name());
        orderService.update(order);
        return new ResponseEntity<>(order, HttpStatus.OK);
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

    @PostMapping("/pay")
    public ResponseEntity<Map<String, Long>> pay(@RequestBody PaymentRequest paymentRequest){
        long transactionId;
        Order order = orderService.get(paymentRequest.getOrderId());

        if (order.getStatus().equals(OrderStatus.PAYMENT_SUCCEEDED.name()))
            throw new BadRequestException("Order already paid");
        if (order.getStatus().equals(OrderStatus.PAYMENT_IN_PROCESS.name()))
            throw new BadRequestException("Order payment in process");
        if (order.getStatus().equals(OrderStatus.DELIVERED.name()))
            throw new BadRequestException("Order already delivered");

        order.setStatus(OrderStatus.PAYMENT_IN_PROCESS.name());
        orderService.update(order);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);

        try {
            PaymentResponse response = restTemplate.postForObject("http://localhost:8081/api/payment/proceed", request, PaymentResponse.class);
            if (response != null)
                transactionId = response.getTransactionId();
            else
                throw new PaymentServiceException("Empty response from payment service");
        } catch (Exception e){
            order.setStatus(OrderStatus.PAYMENT_FAILED.name());
            orderService.update(order);
            throw new PaymentServiceException(e.getMessage());
        }

        order.setTransactionId(transactionId);
        order.setStatus(OrderStatus.PAYMENT_SUCCEEDED.name());
        orderService.update(order);

        Map<String, Long> responseBody = new HashMap<>();
        responseBody.put("transactionId", transactionId);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @Data
    public static class PaymentRequest {
        @NotNull
        private String type;
        @NotNull
        private long orderId;
    }

    @Data
    public static class OrderRequest {
        @NotNull
        private Long orderId;
    }

    @Data
    public static class PaymentResponse {
        private Long transactionId;
    }
}
