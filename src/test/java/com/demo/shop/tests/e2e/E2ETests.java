package com.demo.shop.tests.e2e;

import com.demo.shop.business.models.*;
import com.demo.shop.business.steps.*;
import com.demo.shop.core.BaseTest;
import com.demo.shop.model.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.demo.shop.core.Validator.*;

public class E2ETests extends BaseTest {

    OrderSteps orderSteps = new OrderSteps();
    DeliverySteps deliverySteps = new DeliverySteps();
    CustomerSteps customerSteps = new CustomerSteps();
    ProductSteps productSteps = new ProductSteps();

    @Test
    public void orderStatusTransitionE2eTest(){
        Long customerId = customerSteps.when_getAnyCustomer().getId();
        Long productId = productSteps.when_getAnyProduct().getId();

        OrderModel orderModelReq = OrderModel.builder()
                .customerId(customerId)
                .orderItems(List.of(OrderItemModel.builder().productId(productId).quantity(2).build()))
                .build();

        OrderModel orderRes = orderSteps.when_createOrder(orderModelReq);
        then_validateStatusCode(HttpStatus.CREATED);
        Long orderId = orderRes.getId();

        orderSteps.when_pay(new PaymentModel("card", orderId));
        then_validateStatusCode(HttpStatus.OK);
        orderRes = orderSteps.when_getOrder(orderId);
        Assertions.assertEquals(OrderStatus.PAYMENT_SUCCEEDED.name(), orderRes.getStatus(), "Wrong order status");

        deliverySteps.when_deliver(orderId);
        then_validateStatusCode(HttpStatus.OK);
        orderRes = orderSteps.when_getOrder(orderId);
        Assertions.assertEquals(OrderStatus.DELIVERED.name(), orderRes.getStatus(), "Wrong order status");
    }

    @Test
    public void totalOrderPriceAndNumberOfProductsShouldBeCalculatedProperly(){
        int numberOfProductsInOrder = 2;
        int productN1Quantity = 1;
        int productN2Quantity = 2;

        Long customerId = customerSteps.when_getAnyCustomer().getId();
        ProductModel productN1Res = productSteps.when_getAnyProduct();
        ProductModel productN2Res = productSteps.when_getAnyProduct();

        double productN1price = productN1Res.getPrice();
        double productN2price = productN2Res.getPrice();

        OrderModel orderModelReq = OrderModel.builder()
                .customerId(customerId)
                .orderItems(List.of(
                        OrderItemModel.builder().productId(productN1Res.getId()).quantity(productN1Quantity).build(),
                        OrderItemModel.builder().productId(productN2Res.getId()).quantity(productN2Quantity).build())
                )
                .build();

        Long orderId = orderSteps.when_createOrder(orderModelReq).getId();
        then_validateSuccess();
        OrderModel orderRes = orderSteps.when_getOrder(orderId);
        Assertions.assertEquals(
                numberOfProductsInOrder,
                orderRes.getOrderItems().size(),
                "Wrong products number"
        );
        Assertions.assertEquals(
                numberOfProductsInOrder,
                orderRes.getNumberOfProducts(),
                "Wrong products number"
        );
        Assertions.assertEquals(
                productN1price * productN1Quantity + productN2price * productN2Quantity,
                orderRes.getTotalOrderPrice(),
                "Wrong order price"
        );
    }

    @Test
    public void shouldNotBePossibleToPayForPaidOrder(){
        Long orderId = orderSteps.when_getAnyNewOrder().getId();
        orderSteps.when_pay(new PaymentModel("card", orderId));

        orderSteps.when_pay(new PaymentModel("card", orderId));
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Order already paid");
        Assertions.assertEquals(
                OrderStatus.PAYMENT_SUCCEEDED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status"
        );
    }

    @Test
    public void shouldNotBePossibleToPayForDeliveredOrder(){
        Long orderId = orderSteps.when_getAnyNewOrder().getId();
        orderSteps.when_pay(new PaymentModel("card", orderId));
        deliverySteps.when_deliver(orderId);

        orderSteps.when_pay(new PaymentModel("card", orderId));
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Order already delivered");
        Assertions.assertEquals(
                OrderStatus.DELIVERED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status"
        );
    }

    @Test
    public void shouldNotBePossibleToDeliverNewOrder(){
        Long orderId = orderSteps.when_getAnyNewOrder().getId();

        deliverySteps.when_deliver(orderId);
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "400 : \"{\"errorMessage\":\"Cannot update status for a not paid order\"}\"");
        Assertions.assertEquals(
                OrderStatus.NEW.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status"
        );
    }

    @Test
    public void shouldNotBePossibleToCreateOrderWithDuplicateProducts(){
        Long customerId = customerSteps.when_getAnyCustomer().getId();
        Long productId = productSteps.when_getAnyProduct().getId();
        List<OrderItemModel> orderItems = List.of(
                new OrderItemModel(1, productId),
                new OrderItemModel(1, productId)
        );

        orderSteps.when_createOrder(customerId, orderItems);
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Cannot create order with duplicate product items");
    }

}
