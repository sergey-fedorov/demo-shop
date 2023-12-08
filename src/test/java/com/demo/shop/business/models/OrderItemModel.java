package com.demo.shop.business.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class OrderItemModel {

    private Integer quantity;
    private Long productId;
    private Double totalPrice;

}
