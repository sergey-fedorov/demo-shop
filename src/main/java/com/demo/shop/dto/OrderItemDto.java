package com.demo.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class OrderItemDto {

    private long productId;
    @Min(value = 1, message = "is not valid")
    private int quantity;

}
