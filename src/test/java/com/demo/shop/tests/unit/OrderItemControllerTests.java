package com.demo.shop.tests.unit;

import com.demo.shop.controller.OrderItemController;
import com.demo.shop.model.*;
import com.demo.shop.service.OrderItemService;
import com.demo.shop.service.OrderService;
import com.demo.shop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderItemController.class)
class OrderItemControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrderService orderService;

    @MockBean
    OrderItemService orderItemService;

    @MockBean
    ProductService productService;


    String anyString = "anyString";
    Customer customer = Customer.builder().id(1L).email(anyString).address(anyString).fullName(anyString).build();
    Product product = Product.builder().id(1L).name(anyString).price(5.0).pictureUrl(anyString).build();
    Product productTwo = Product.builder().id(2L).name(anyString).price(5.0).pictureUrl(anyString).build();
    Order order = Order.builder().dateCreated(LocalDateTime.now()).customer(customer).status(OrderStatus.NEW.name()).build();
    OrderItem orderItem = new OrderItem(order, product, 2);
    OrderItem orderItemTwo = new OrderItem(order, productTwo, 2);

    OrderItemController.OrderItemForm orderItemFormBody = new OrderItemController.OrderItemForm();

    @BeforeEach
    void setUp() {
        order.setOrderItems(new ArrayList<>(List.of(orderItem, orderItemTwo)));
        orderItemFormBody.setOrderId(1L).setCustomerId(1).setQuantity(1).setProductId(1);

        doNothing().when(orderItemService).delete(new OrderItemPK());
        when(orderService.get(anyLong())).thenReturn(order);
        when(orderItemService.create(orderItem)).thenReturn(orderItem);
        when(productService.getProduct(anyLong())).thenReturn(product);
    }


    @Test
    void deleteOrderItem_validOrderItem_shouldBeDeleted() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormBody))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteOrderItem_wrongCustomerId_shouldThrowForbidden() throws Exception {
        OrderItemController.OrderItemForm orderItemFormWrongCustomerId = orderItemFormBody.setCustomerId(2);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormWrongCustomerId))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage").value("Order doesn't belong to specified customer"));
    }

    @Test
    void deleteOrderItem_notNewOrderStatus_shouldThrowBadRequest() throws Exception {
        order.setStatus(OrderStatus.DELIVERED.name());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormBody))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Cannot remove item from a not new order"));
    }

    @Test
    void deleteOrderItem_notLastOrderItem_orderShouldNotBeDeleted() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormBody))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        verify(orderService, times(0)).delete(null);
    }

    @Test
    void deleteOrderItem_lastOrderItem_orderShouldBeDeleted() throws Exception {
        // one order item in order
        order.setOrderItems(List.of(orderItem));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormBody))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        verify(orderService, times(1)).delete(null);
    }

    @Test
    void deleteOrderItem_valuesLessThan1_shouldThrowBadRequest() throws Exception {
        OrderItemController.OrderItemForm orderItemFormInvalidValues =
                orderItemFormBody.setOrderId(0).setCustomerId(0).setQuantity(0).setProductId(0);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormInvalidValues))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteOrderItem_missingFields_shouldThrowBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteOrderItem_missingOrderIdField_shouldThrowBadRequest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content("{\"customerId\":1,\"productId\":1,\"quantity\":1}")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrderItem_validOrderItem_shouldBeCreated() throws Exception {
        OrderItemController.OrderItemForm orderItemFormThirdValid =
                orderItemFormBody.setOrderId(1L).setCustomerId(1).setQuantity(1).setProductId(3);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormThirdValid))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderItems", hasSize(3)));
    }

    @Test
    void createOrderItem_wrongCustomerId_shouldThrowForbidden() throws Exception {
        OrderItemController.OrderItemForm orderItemFormWrongCustomerId = orderItemFormBody.setCustomerId(2);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormWrongCustomerId))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage").value("Order doesn't belong to specified customer"));
    }

    @Test
    void createOrderItem_notNewOrderStatus_shouldThrowBadRequest() throws Exception {
        order.setStatus(OrderStatus.DELIVERED.name());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormBody))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Cannot add item to a not new order"));
    }

    @Test
    void createOrderItem_existingOrderItem_shouldThrowBadRequest() throws Exception {
        OrderItemController.OrderItemForm orderItemFormExistingOrderItem = orderItemFormBody.setProductId(1L);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemFormExistingOrderItem))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Cannot add item with product that already added to order"));
    }


}
