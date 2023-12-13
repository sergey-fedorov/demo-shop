package com.demo.shop.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class OrderItemDto {

    private long productId;
    private int quantity;

}
