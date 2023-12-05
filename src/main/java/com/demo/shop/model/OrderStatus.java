package com.demo.shop.model;

import java.util.Arrays;

public enum OrderStatus {
    NEW,
    PAYMENT_IN_PROCESS,
    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED,
    DELIVERED;

    public boolean contains(String status){
        return Arrays.stream(OrderStatus.values()).map(Enum::name).toList().contains(status.toUpperCase());
    }

}
