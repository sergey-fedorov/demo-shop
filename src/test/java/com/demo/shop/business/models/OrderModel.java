package com.demo.shop.business.models;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderModel {

    private List<OrderItemModel> orderItems;
    private Long id;
    private String dateCreated;
    private String status;
    private Long transactionId;
    private Long customerId;
    private Double totalOrderPrice;
    private Integer numberOfProducts;


    // Could not write JSON: Infinite recursion
    /*public OrderModel getFake(){
        Faker faker = new Faker();
        return OrderModel.builder()
//                .id(faker.number().randomNumber())
                .dateCreated(faker.date().toString())
                .status("DELIVERED")
                .transactionId(faker.number().randomNumber())
                .customerId(faker.number().randomNumber())
                .totalOrderPrice(faker.number().randomDouble(1, 1, 100))
                .numberOfProducts(faker.number().randomDigitNotZero())
                .orderItems(
                        List.of(OrderItemModel.builder()
                                .productId(faker.number().randomNumber())
                                .quantity(faker.number().randomDigitNotZero())
                                .totalPrice(faker.number().randomDouble(1, 1, 100))
                                .build()
                        )
                )
                .build();
    }*/
}
