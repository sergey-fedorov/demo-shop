package com.demo.shop.tests.component;

import com.demo.shop.business.models.CustomerModel;
import com.demo.shop.business.models.OrderItemModel;
import com.demo.shop.business.models.OrderModel;
import com.demo.shop.business.models.PaymentModel;
import com.demo.shop.business.steps.CustomerSteps;
import com.demo.shop.business.steps.OrderSteps;
import com.demo.shop.business.steps.ProductSteps;
import com.demo.shop.core.RequestSpecificationFactory;
import com.demo.shop.model.OrderStatus;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static com.demo.shop.core.Validator.then_validateStatusCode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@WireMockTest(httpPort = 8181)
@Testcontainers
public class DemoShopServiceTests {

    static String mockServer = "http://localhost:8181";

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.1.0");

    @DynamicPropertySource
    static void dynamicConfiguration(DynamicPropertyRegistry registry) {
        registry.add("email.validator.service", () -> mockServer);
        registry.add("api.payment.service", () -> mockServer);
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeAll
    public static void setup() {
        RequestSpecificationFactory.setBaseUri("http://localhost");
    }

    CustomerSteps customerSteps = new CustomerSteps();
    OrderSteps orderSteps = new OrderSteps();
    ProductSteps productSteps = new ProductSteps();


    @Test
    void newCustomerCreatedWithValidDetails() {
        CustomerModel customerReqBody = CustomerModel.getFake();

        CustomerModel customerResBody = customerSteps.when_createCustomer(customerReqBody);
        then_validateStatusCode(HttpStatus.CREATED);
        Assertions.assertEquals(customerReqBody, customerResBody);

        customerResBody = customerSteps.when_getCustomerDetails(customerResBody.getId());
        Assertions.assertEquals(customerReqBody, customerResBody);
    }

    @Test
    void statusShouldBeChangedFromNewToSucceededToDelivered(){
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

        orderSteps.when_updateStatus(orderId);
        then_validateStatusCode(HttpStatus.OK);
        Assertions.assertEquals(
                OrderStatus.DELIVERED.name(),
                orderSteps.when_getOrder(orderId).getStatus(),
                "Wrong order status");
    }


}
