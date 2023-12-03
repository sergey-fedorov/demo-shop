package com.demo.shop.dto;

import lombok.Data;

@Data
public class OrderItemDto {

    private long productId;
    private int quantity;

}
