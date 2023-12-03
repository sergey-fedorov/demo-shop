package com.demo.shop.controller;

import com.demo.shop.exception.BadRequestException;
import com.demo.shop.model.Order;
import com.demo.shop.model.OrderStatus;
import com.demo.shop.service.OrderService;
import com.demo.shop.service.PaymentService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;
    @Autowired
    OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> pay(@RequestBody PaymentForm paymentForm){
        long transactionId;
        Order order = orderService.get(paymentForm.getOrderId());

        if (order.getStatus().equals(OrderStatus.PAYMENT_SUCCEEDED.name()))
            throw new BadRequestException("Order already paid");
        if (order.getStatus().equals(OrderStatus.PAYMENT_IN_PROCESS.name()))
            throw new BadRequestException("Order payment in process");

        order.setStatus(OrderStatus.PAYMENT_IN_PROCESS.name());
        orderService.update(order);

        try {
            transactionId = paymentService.proceed(paymentForm.getType());
        } catch (Exception e){
            order.setStatus(OrderStatus.PAYMENT_FAILED.name());
            orderService.update(order);
            throw new BadRequestException(e.getMessage());
        }

        order.setTransactionId(transactionId);
        order.setStatus(OrderStatus.PAYMENT_SUCCEEDED.name());
        orderService.update(order);

        Map<String, Long> responseBody = new HashMap<>();
        responseBody.put("transactionId", transactionId);

        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }


    @Data
    public static class PaymentForm {
        private String type;
        private long orderId;
    }
}
