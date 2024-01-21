package com.demo.shop.tests.e2e;

import com.demo.shop.business.models.*;
import com.demo.shop.business.steps.*;
import com.demo.shop.core.BaseTest;
import com.demo.shop.model.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.text.DecimalFormat;
import java.util.List;

import static com.demo.shop.core.Validator.*;

public class OrderTests extends BaseTest {

    OrderSteps orderSteps = new OrderSteps();
    DeliverySteps deliverySteps = new DeliverySteps();
    CustomerSteps customerSteps = new CustomerSteps();
    ProductSteps productSteps = new ProductSteps();
    DecimalFormat twoDecimalPlaces = new DecimalFormat("#.##");

    @Test
    public void statusShouldBeChangedFromNewToSucceededToDelivered(){
        Long customerId = customerSteps.when_getAnyCustomer().getId();
        Long productId = productSteps.when_getAnyProduct().getId();

        OrderModel orderModelReq = OrderModel.builder()
                .customerId(customerId)
                .orderItems(List.of(OrderItemModel.builder().productId(productId).quantity(2).build()))
                .build();

        Long orderId = orderSteps.when_createOrder(orderModelReq).getId();
        then_validateStatusCode(HttpStatus.CREATED);
        Assertions.assertEquals(
                OrderStatus.NEW.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status");

        orderSteps.when_pay(new PaymentModel("card", orderId));
        then_validateStatusCode(HttpStatus.OK);
        Assertions.assertEquals(
                OrderStatus.PAYMENT_SUCCEEDED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status");

        deliverySteps.when_deliver(orderId);
        then_validateStatusCode(HttpStatus.OK);
        Assertions.assertEquals(
                OrderStatus.DELIVERED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status");
    }

    @Test
    public void statusShouldBeChangedFromNewToFailedToSucceeded(){
        Long orderId = orderSteps.when_getAnyNewOrder().getId();

        orderSteps.when_pay(new PaymentModel("any", orderId));
        then_validateStatusCode(HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(
                OrderStatus.PAYMENT_FAILED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status");

        orderSteps.when_pay(new PaymentModel("card", orderId));
        then_validateStatusCode(HttpStatus.OK);
        Assertions.assertEquals(
                OrderStatus.PAYMENT_SUCCEEDED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status");
    }

    @Test
    public void shouldNotBePossibleToPayForPaidOrderAndStatusRemains(){
        Long orderId = orderSteps.when_getAnyNewOrder().getId();
        orderSteps.when_pay(new PaymentModel("card", orderId));

        orderSteps.when_pay(new PaymentModel("card", orderId));
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Order already paid");
        Assertions.assertEquals(
                OrderStatus.PAYMENT_SUCCEEDED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status"
        );

        orderSteps.when_pay(new PaymentModel("any", orderId));
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Order already paid");
        Assertions.assertEquals(
                OrderStatus.PAYMENT_SUCCEEDED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status"
        );
    }

    @Test
    public void shouldNotBePossibleToDeliverOrPayForDeliveredOrderAndStatusRemains(){
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

        orderSteps.when_pay(new PaymentModel("any", orderId));
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "Order already delivered");
        Assertions.assertEquals(
                OrderStatus.DELIVERED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status"
        );

        deliverySteps.when_deliver(orderId);
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "400 : \"{\"errorMessage\":\"Cannot update status for a not paid order\"}\"");
        Assertions.assertEquals(
                OrderStatus.DELIVERED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status"
        );

    }

    @Test
    public void shouldNotBePossibleToDeliverNewOrderAndStatusRemains(){
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
    public void shouldNotBePossibleToDeliverFailedOrderAndStatusRemains(){
        Long orderId = orderSteps.when_getAnyNewOrder().getId();
        orderSteps.when_pay(new PaymentModel("any", orderId));

        deliverySteps.when_deliver(orderId);
        then_validateErrorResponse(HttpStatus.BAD_REQUEST, "400 : \"{\"errorMessage\":\"Cannot update status for a not paid order\"}\"");
        Assertions.assertEquals(
                OrderStatus.PAYMENT_FAILED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status"
        );

        orderSteps.when_pay(new PaymentModel("any", orderId));
        then_validateStatusCode(HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(
                OrderStatus.PAYMENT_FAILED.name(),
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
                Double.valueOf(twoDecimalPlaces.format(productN1price * productN1Quantity + productN2price * productN2Quantity)),
                orderRes.getTotalOrderPrice(),
                "Wrong order price"
        );
        Assertions.assertEquals(
                Double.valueOf(twoDecimalPlaces.format(productN1price * productN1Quantity + productN2price * productN2Quantity)),
                orderRes.getOrderItems().stream().mapToDouble(OrderItemModel::getTotalPrice).sum(),
                "Wrong order price"
        );
    }

    @Test
    public void orderTransactionIdShouldBeSetAfterPaymentSucceeded(){
        Long orderId = orderSteps.when_getAnyNewOrder().getId();
        Assertions.assertNull(orderSteps.when_getOrder(orderId).getTransactionId());

        orderSteps.when_pay(new PaymentModel("any", orderId));
        then_validateStatusCode(HttpStatus.BAD_REQUEST);
        Assertions.assertNull(orderSteps.when_getOrder(orderId).getTransactionId());

        orderSteps.when_pay(new PaymentModel("card", orderId));
        then_validateStatusCode(HttpStatus.OK);
        Assertions.assertNotNull(orderSteps.when_getOrder(orderId).getTransactionId());
    }

    @Test
    public void totalOrderItemPriceAndNumberOfProductsShouldBeCalculatedProperly(){
        Long orderId = orderSteps.when_getAnyNewOrder().getId();
        OrderModel orderRes = orderSteps.when_getOrder(orderId);

        orderRes.getOrderItems().forEach(item -> {
            double productPrice = productSteps.when_getProductById(item.getProductId()).getPrice();
            Assertions.assertEquals(
                    Double.valueOf(twoDecimalPlaces.format(productPrice * item.getQuantity())),
                    item.getTotalPrice(),
                    "Wrong order item price"
            );
        });
    }

}
