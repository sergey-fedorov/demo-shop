package com.demo.shop.tests.contract;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.target.TestTarget;
import au.com.dius.pact.provider.spring.target.MockMvcTarget;
import com.demo.shop.business.models.OrderItemModel;
import com.demo.shop.business.models.OrderModel;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RunWith(PactRunner.class)
@Provider("orders_provider")
@PactFolder("src/test/resources/pacts")
public class ProviderOrdersServiceTestMockTestModel {

    /* Using mock service with test model
    * Issue with OrderModel builder: Could not write JSON: Infinite recursion
    * */

    @InjectMocks
    private OrderControllerMock orderControllerMock = new OrderControllerMock();

    @TestTarget
    public final MockMvcTarget target = new MockMvcTarget();

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        target.setControllers(orderControllerMock);
        target.setPrintRequestResponse(true);
    }

    @State("test updateOrderStatus")
    public void myTest() {
    }

    @RestController
    static class OrderControllerMock {
        Faker faker = new Faker();
        OrderModel orderRes = new OrderModel(
                List.of(new OrderItemModel(
                        faker.number().randomDigitNotZero(),
                        faker.number().randomNumber(),
                        faker.number().randomDouble(1, 1, 100))
                ),
                faker.number().randomNumber(),
                faker.date().birthday().toString(),
                "DELIVERED",
                faker.number().randomNumber(),
                faker.number().randomNumber(),
                faker.number().randomDouble(1, 1, 100),
                faker.number().randomDigitNotZero()
        );

        @GetMapping("/api/orders")
        @ResponseStatus(HttpStatus.OK)
        List<OrderModel> getOrders(@RequestParam(required = false) String status) {
            return List.of(orderRes);
        }

        @PostMapping("/api/orders/status")
        @ResponseStatus(HttpStatus.OK)
        OrderModel updateStatus(@Valid @RequestBody OrderRequest orderRequest) {
            return orderRes;
        }
    }

    @Data
    public static class OrderRequest {
        @NotNull
        private Long orderId;
    }

}
