package com.demo.shop.tests.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactDirectory;
import com.demo.shop.business.steps.OrderSteps;
import com.demo.shop.core.RequestSpecificationFactory;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Objects;

@PactConsumerTest
@PactDirectory("src/test/resources/pacts")
public class DeliveryConsumerPactTest {


    @Pact(provider = "orders_provider", consumer = "delivery_consumer")
    public RequestResponsePact getPaidOrders(PactDslWithProvider builder) {
        return builder
                .uponReceiving("Orders list from api-orders")
                .path("/api/orders")
                .query("status=PAYMENT_SUCCEEDED")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(Objects.requireNonNull(
                        Objects.requireNonNull(PactDslJsonArray.arrayEachLike()
                                .id("id")
                                .id("customerId")
                                .integerType("numberOfProducts")
                                .eachLike("orderItems")
                                .integerType("quantity")
                                .numberType("totalPrice", 1.0)
                                .id("productId")
                                .closeArray())
                        .closeObject())
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getPaidOrders", pactVersion = PactSpecVersion.V3)
    void testTetPaidOrders(MockServer mockServer) {
        OrderSteps orderSteps = new OrderSteps();
        RequestSpecificationFactory.mock(mockServer.getUrl(), mockServer.getPort());

        orderSteps.when_getOrderListByStatus("PAYMENT_SUCCEEDED");
        orderSteps.then_validateStatusCode(HttpStatus.OK);
    }


    @Pact(provider = "orders_provider", consumer = "delivery_consumer")
    public RequestResponsePact updateOrderStatus(PactDslWithProvider builder) throws JSONException {
        return builder
                .given("test updateOrderStatus")
                .uponReceiving("Update order status in api-orders")
                .path("/api/orders/status")
                .headers(Map.of("Content-Type", "application/json"))
//                .body(new JSONObject().put("orderId", 111))
                // To rewrite value in provider test
                .body(new PactDslJsonBody().valueFromProviderState("orderId", "orderId", 1))
                .method("POST")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .id("id")
                        .stringType("status")
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "updateOrderStatus", pactVersion = PactSpecVersion.V3)
    void tesUpdateOrderStatus(MockServer mockServer) {
        OrderSteps orderSteps = new OrderSteps();
        RequestSpecificationFactory.mock(mockServer.getUrl(), mockServer.getPort());

        orderSteps.when_updateStatus(111L);
        orderSteps.then_validateStatusCode(HttpStatus.OK);
    }

}
