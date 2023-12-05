package com.demo.shop.service;


import com.demo.shop.exception.ResourceNotFoundException;
import com.demo.shop.model.Order;
import com.demo.shop.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;


    @Override
    public List<Order> getAllOrders() {
        Iterable<Order> orderIterable = orderRepository.findAll();
        return StreamSupport.stream(orderIterable.spliterator(), false).toList();
    }

    @Override
    public Order get(final Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order create(Order order) {
        order.setDateCreated(LocalDateTime.now());
        return this.orderRepository.save(order);
    }

    @Override
    public void update(Order order) {
        this.orderRepository.save(order);
    }


}
