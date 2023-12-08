package com.demo.shop.tests.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.demo.shop.business.models.OrderItemModel;
import com.demo.shop.business.models.OrderModel;
import com.demo.shop.business.models.PaymentModel;
import com.demo.shop.business.steps.OrderSteps;
import com.demo.shop.business.steps.PaymentSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Provider("orders_provider")
@PactFolder("src/test/resources/pacts")
public class OrdersProviderPactTestReal {

   /* Using running service */

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", 8081, "/"));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("test updateOrderStatus")
    public Map<String, Long> myTest() {
        Long id = setup();

        Map<String, Long> map = new HashMap<>();
        map.put("orderId", id);
        return map;
    }

    public Long setup(){
        OrderSteps orderSteps = new OrderSteps();
        PaymentSteps paymentSteps = new PaymentSteps();

        OrderModel orderModelReq = OrderModel.builder()
                .customerId(1L)
                .orderItems(List.of(OrderItemModel.builder().productId(1L).quantity(10).build()))
                .build();

        OrderModel orderRes = orderSteps.when_createOrder(orderModelReq);
        paymentSteps.when_pay(new PaymentModel("card", orderRes.getId()));

        return orderRes.getId();
    }


}
