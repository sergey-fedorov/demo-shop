package com.demo.shop.service;


import com.demo.shop.model.OrderItem;
import com.demo.shop.model.OrderItemPK;
import com.demo.shop.repository.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;


    @Override
    public OrderItem create(OrderItem orderItem) {
        return this.orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItem get(OrderItemPK orderItemPK) {
        return null;
    }

    @Override
    public void delete(OrderItemPK orderItemPK) {
        this.orderItemRepository.deleteById(orderItemPK);
    }
}
