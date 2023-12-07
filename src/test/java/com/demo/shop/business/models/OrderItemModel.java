package com.demo.shop.business.models;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class OrderItemModel {

    private Integer quantity;
    private Integer productId;
    private Double totalPrice;

}
