package com.demo.shop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class OrderItem {

    @EmbeddedId
    @JsonIgnore
    private OrderItemPK pk;

    @Column(nullable = false)
    private int quantity;

    public OrderItem() {
        super();
    }

    public OrderItem(Order order, Product product, int quantity) {
        pk = new OrderItemPK();
        pk.setOrder(order);
        pk.setProduct(product);
        this.quantity = quantity;
    }

    @JsonIgnore
    public Product getProduct() {
        return this.pk.getProduct();
    }

    @JsonIgnore
    public Order getOrder() {
        return this.pk.getOrder();
    }

    @Transient
    public long getProductId() {
        return getProduct().getId();
    }

    @Transient
    public Double getTotalPrice() {
        return getProduct().getPrice() * getQuantity();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pk == null) ? 0 : pk.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OrderItem other = (OrderItem) obj;
        if (pk == null) {
            return other.pk == null;
        } else
            return pk.equals(other.pk);
    }
}
