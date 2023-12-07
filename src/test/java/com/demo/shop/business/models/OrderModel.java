package com.demo.shop.business.models;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class OrderModel {

    private List<OrderItemModel> orderItems;
    private Long id;
    private String dateCreated;
    private String status;
    private Long transactionId;
    private Long customerId;
    private Double totalOrderPrice;
    private Integer numberOfProducts;
    
}
