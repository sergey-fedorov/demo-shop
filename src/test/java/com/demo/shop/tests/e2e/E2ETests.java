package com.demo.shop.tests.e2e;

import com.demo.shop.business.models.*;
import com.demo.shop.business.steps.*;
import com.demo.shop.core.BaseTest;
import com.demo.shop.model.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

public class E2ETests extends BaseTest {

    OrderSteps orderSteps = new OrderSteps();
    PaymentSteps paymentSteps = new PaymentSteps();
    DeliverySteps deliverySteps = new DeliverySteps();
    CustomerSteps customerSteps = new CustomerSteps();
    ProductSteps productSteps = new ProductSteps();

    @Test
    public void createPayDeliverOrderE2E(){
        Long customerId = customerSteps.when_createCustomer(CustomerModel.getFake()).getId();
        Long productId1 = productSteps.when_createProduct(ProductModel.getFake()).getId();
        Long productId2 = productSteps.when_createProduct(ProductModel.getFake()).getId();

        OrderModel orderModelReq = OrderModel.builder()
                .customerId(customerId)
                .orderItems(List.of(
                        OrderItemModel.builder().productId(productId1).quantity(2).build(),
                        OrderItemModel.builder().productId(productId2).quantity(4).build())
                )
                .build();

        OrderModel orderRes = orderSteps.when_createOrder(orderModelReq);
        orderSteps.then_validateStatusCode(HttpStatus.CREATED);
        Long orderId = orderRes.getId();

        paymentSteps.when_pay(new PaymentModel("card", orderId));
        orderSteps.then_validateStatusCode(HttpStatus.CREATED);
        orderRes = orderSteps.when_getOrder(orderId);
        Assertions.assertEquals(OrderStatus.PAYMENT_SUCCEEDED.name(), orderRes.getStatus(), "Wrong order status");

        deliverySteps.when_deliver(orderId);
        orderSteps.then_validateStatusCode(HttpStatus.OK);
        orderRes = orderSteps.when_getOrder(orderId);
        Assertions.assertEquals(OrderStatus.DELIVERED.name(), orderRes.getStatus(), "Wrong order status");
    }

}
