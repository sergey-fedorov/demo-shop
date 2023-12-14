package com.demo.shop.tests.contract;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.target.TestTarget;
import au.com.dius.pact.provider.spring.target.MockMvcTarget;
import com.demo.shop.controller.OrderController;
import com.demo.shop.model.Order;
import com.demo.shop.service.OrderServiceImpl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(PactRunner.class)
@Provider("orders_provider")
@PactFolder("src/test/resources/pacts")
public class ProviderOrdersServiceTestMockAppModel {

    /* Using mock service with app model
     * How to return Order with all field?
     * currently returned:
     * {"orderItems":null,"id":1,"dateCreated":null,"status":"PAYMENT_SUCCEEDED","transactionId":999}
     * Why 500 error returned in MockHttpServletResponse?
     *
     * https://github.com/pact-foundation/pact-jvm/blob/c002e9a977c09e0c677df97cbdb252d3ea02cea5/provider/spring/src/test/java/au/com/dius/pact/provider/spring/BooksPactProviderTest.java#L49
     * */

    @InjectMocks
    private OrderController orderController = new OrderController();

    @Mock
    private OrderServiceImpl orderService;

    @TestTarget
    public final MockMvcTarget target = new MockMvcTarget();

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        target.setControllers(orderController);
        target.setPrintRequestResponse(true);

        Order order = Order.builder()
                .id(1L)
                .transactionId(999L)
                .status("PAYMENT_SUCCEEDED")
                .build();

        when(orderService.getAllOrders()).thenReturn(List.of(order));
        when(orderService.get(1L)).thenReturn(order);
    }

    @State("test updateOrderStatus")
    public void myTest() {
    }
}
