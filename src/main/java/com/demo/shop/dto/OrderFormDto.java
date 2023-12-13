package com.demo.shop.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class OrderFormDto {
    private long customerId;
    private List<OrderItemDto> orderItems;
}
