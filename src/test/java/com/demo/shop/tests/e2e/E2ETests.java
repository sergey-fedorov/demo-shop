package com.demo.shop.tests.e2e;

import com.demo.shop.business.models.OrderItemModel;
import com.demo.shop.business.models.OrderModel;
import com.demo.shop.business.models.PaymentModel;
import com.demo.shop.business.steps.DeliverySteps;
import com.demo.shop.business.steps.OrderSteps;
import com.demo.shop.business.steps.PaymentSteps;
import com.demo.shop.model.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

public class E2ETests {

    OrderSteps orderSteps = new OrderSteps();
    PaymentSteps paymentSteps = new PaymentSteps();
    DeliverySteps deliverySteps = new DeliverySteps();

    OrderModel orderModelReq = OrderModel.builder()
            .customerId(1L)
            .orderItems(List.of(
                    OrderItemModel.builder().productId(1L).quantity(10).build(),
                    OrderItemModel.builder().productId(2L).quantity(20).build())
            )
            .build();

    @Test
    public void createPayDeliverOrderE2E(){
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
