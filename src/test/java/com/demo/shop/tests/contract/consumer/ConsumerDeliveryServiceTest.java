package com.demo.shop.tests.contract.consumer;

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
import io.restassured.RestAssured;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static io.restassured.RestAssured.given;

@PactConsumerTest
@PactDirectory("src/test/resources/pacts")
public class ConsumerDeliveryServiceTest {

    @BeforeAll
    static void setup(){
        System.setProperty("pact.writer.overwrite", "true");
    }

    @Pact(provider = "orders_provider", consumer = "delivery_consumer")
    public RequestResponsePact getPaidOrders(PactDslWithProvider builder) {
        return builder
                .given("orders with PAYMENT_SUCCEEDED and other statuses exist")
                .uponReceiving("a request to get the list of PAYMENT_SUCCEEDED orders from api-orders")
                .path("/api/orders")
                .query("status=PAYMENT_SUCCEEDED")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(PactDslJsonArray.arrayMinLike(1)
                        .equalTo("status", "PAYMENT_SUCCEEDED")
                        .id("id")
                        .id("customerId")
                        .integerType("numberOfProducts")
                        .eachLike("orderItems")
                            .integerType("quantity")
                            .numberType("totalPrice", 1.0)
                            .id("productId"))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getPaidOrders", pactVersion = PactSpecVersion.V3)
    void testGetPaidOrders(MockServer mockServer) {
        RestAssured.baseURI = mockServer.getUrl();
        given().queryParams("status", "PAYMENT_SUCCEEDED")
                .get("/api/orders")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = "orders_provider", consumer = "delivery_consumer")
    public RequestResponsePact getPaidOrders_noOrders(PactDslWithProvider builder) {
        return builder
                .given("no orders with status PAYMENT_SUCCEEDED exist")
                .uponReceiving("a request to get the empty list of orders from api-orders")
                .path("/api/orders")
                .query("status=PAYMENT_SUCCEEDED")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body("[]")
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getPaidOrders_noOrders", pactVersion = PactSpecVersion.V3)
    void testNoOrders(MockServer mockServer) {
        RestAssured.baseURI = mockServer.getUrl();
        given().queryParams("status", "PAYMENT_SUCCEEDED")
                .get("/api/orders")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }


    @Pact(provider = "orders_provider", consumer = "delivery_consumer")
    public RequestResponsePact updateOrderStatus(PactDslWithProvider builder) {
        return builder
                .given("order with status PAYMENT_SUCCEEDED exists")
                .uponReceiving("a request to update order status")
                .path("/api/orders/status")
                .headers(Map.of("Content-Type", "application/json"))
                // To rewrite value in provider test
                .body(new PactDslJsonBody().valueFromProviderState("orderId", "orderId", 1))
                .method("POST")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .id("id")
                        .stringType("status"))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "updateOrderStatus", pactVersion = PactSpecVersion.V3)
    void tesUpdateOrderStatus(MockServer mockServer) {
        RestAssured.baseURI = mockServer.getUrl();
        given().body(Map.of("orderId", 1))
                .header("Content-Type", "application/json")
                .post("/api/orders/status")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = "orders_provider", consumer = "delivery_consumer")
    public RequestResponsePact updateOrderStatus_orderNotFound(PactDslWithProvider builder) throws JSONException {
        return builder
                .given("order with id 999 does not exist")
                .uponReceiving("a request to update order status")
                .path("/api/orders/status")
                .headers(Map.of("Content-Type", "application/json"))
                .body(new JSONObject().put("orderId", 999))
                .method("POST")
                .willRespondWith()
                .status(404)
                .body(new PactDslJsonBody()
                        .stringType("errorMessage"))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "updateOrderStatus_orderNotFound", pactVersion = PactSpecVersion.V3)
    void testUpdateOrderStatus_orderNotFound(MockServer mockServer) {
        RestAssured.baseURI = mockServer.getUrl();
        given().body(Map.of("orderId", 999))
                .header("Content-Type", "application/json")
                .post("/api/orders/status")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}
