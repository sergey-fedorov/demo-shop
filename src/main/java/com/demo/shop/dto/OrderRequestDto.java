package com.demo.shop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class OrderRequestDto {
    @Min(value = 1L, message = "is not valid")
    private long customerId;
    @Valid
    private List<OrderItemDto> orderItems;
}
