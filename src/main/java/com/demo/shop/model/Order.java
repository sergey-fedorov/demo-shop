package com.demo.shop.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "orders") @Data
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="orderItems")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreated;

    private String status;

    // null is default value for db
    private Long transactionId = null;


    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Customer customer;

    @OneToMany(mappedBy = "pk.order")
    @Valid
    private List<OrderItem> orderItems;

    @Transient
    public Double getTotalOrderPrice() {
        double sum = 0D;
        List<OrderItem> orderItems = getOrderItems();
        for (OrderItem op : orderItems) {
            sum += op.getTotalPrice();
        }
        return Double.valueOf(new DecimalFormat("#.##").format(sum));
    }

    @Transient
    public int getNumberOfProducts() {
        return this.orderItems.size();
    }

    @Transient
    public long getCustomerId() {
        return this.customer.getId();
    }
}
