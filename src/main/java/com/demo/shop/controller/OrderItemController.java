package com.demo.shop.controller;

import com.demo.shop.exception.BadRequestException;
import com.demo.shop.exception.ForbiddenException;
import com.demo.shop.model.Order;
import com.demo.shop.model.OrderItem;
import com.demo.shop.model.OrderItemPK;
import com.demo.shop.model.OrderStatus;
import com.demo.shop.service.OrderItemService;
import com.demo.shop.service.OrderService;
import com.demo.shop.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/order-items")
@Tag(name = "api-order-items", description = "Demo-shop")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    @DeleteMapping
    public ResponseEntity<Void> deleteOrderItem(@Valid @RequestBody OrderItemForm orderItemForm){
        Order order = orderService.get(orderItemForm.getOrderId());

        Long orderOwnerCustomerId = order.getCustomer().getId();
        if (orderOwnerCustomerId != orderItemForm.getCustomerId())
            throw new ForbiddenException("Order doesn't belong to specified customer");

        String orderStatus = order.getStatus();
        if (!orderStatus.equals(OrderStatus.NEW.name()))
            throw new BadRequestException("Cannot remove item from a not new order");

        OrderItemPK orderItemPK = new OrderItemPK();
        orderItemPK.setOrder(order);
        orderItemPK.setProduct(productService.getProduct(orderItemForm.getProductId()));
        orderItemService.delete(orderItemPK);

        if (order.getOrderItems().size() == 1)
            orderService.delete(order.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Order> createOrderItem(@RequestBody @Valid OrderItemForm orderItemForm){
        Order order = orderService.get(orderItemForm.getOrderId());

        Long orderOwnerCustomerId = order.getCustomer().getId();
        if (orderOwnerCustomerId != orderItemForm.getCustomerId())
            throw new ForbiddenException("Order doesn't belong to specified customer");

        String orderStatus = order.getStatus();
        if (!orderStatus.equals(OrderStatus.NEW.name()))
            throw new BadRequestException("Cannot add item to a not new order");

        boolean doesProductExistInOrder = order.getOrderItems().stream().anyMatch(oi -> oi.getProduct().getId().equals(orderItemForm.getProductId()));
        if (doesProductExistInOrder)
            throw new BadRequestException("Cannot add item with product that already added to order");

        OrderItem orderItem = new OrderItem(order, productService.getProduct(orderItemForm.getProductId()), orderItemForm.getQuantity());
        orderItemService.create(orderItem);
        order.getOrderItems().add(orderItem);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }


    @Data @Accessors(chain = true)
    public static class OrderItemForm {
        @Min(1)
        private long customerId;
        @Min(1)
        private long orderId;
        @Min(1)
        private long productId;
        @Min(1)
        private int quantity;
    }

}
