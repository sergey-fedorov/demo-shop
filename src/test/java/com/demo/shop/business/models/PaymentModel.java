package com.demo.shop.business.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class PaymentModel {

    private String type;
    private Long orderId;

}
