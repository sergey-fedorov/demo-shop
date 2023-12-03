package com.demo.shop.repository;

import com.demo.shop.model.OrderItem;
import com.demo.shop.model.OrderItemPK;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, OrderItemPK> {
}
