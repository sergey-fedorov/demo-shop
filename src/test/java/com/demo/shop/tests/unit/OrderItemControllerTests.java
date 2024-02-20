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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderItemController.class)
public class OrderItemControllerTests {

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

    OrderItemController.OrderItemForm orderItemForm = new OrderItemController.OrderItemForm();

    @BeforeEach
    public void setUp() {
        order.setOrderItems(List.of(orderItem, orderItemTwo));
        orderItemForm.setOrderId(1L).setCustomerId(1).setQuantity(1).setProductId(1);
    }


    @Test
    public void deleteOrderItem_validOrderItem_shouldBeDeleted() throws Exception {
        doNothing().when(orderItemService).delete(new OrderItemPK());
        when(orderService.get(anyLong())).thenReturn(order);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemForm))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteOrderItem_wrongCustomerId_shouldThrowForbidden() throws Exception {
        orderItemForm.setCustomerId(2);

        doNothing().when(orderItemService).delete(new OrderItemPK());
        when(orderService.get(anyLong())).thenReturn(order);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemForm))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage").value("Order doesn't belong to specified customer"));
    }

    @Test
    public void deleteOrderItem_notNewOrderStatus_shouldThrowBadRequest() throws Exception {
        order.setStatus(OrderStatus.DELIVERED.name());

        doNothing().when(orderItemService).delete(new OrderItemPK());
        when(orderService.get(anyLong())).thenReturn(order);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemForm))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Cannot remove item from a not new order"));
    }

    @Test
    public void deleteOrderItem_notLastOrderItem_orderShouldNotBeDeleted() throws Exception {
        doNothing().when(orderItemService).delete(new OrderItemPK());
        when(orderService.get(anyLong())).thenReturn(order);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemForm))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        verify(orderService, times(0)).delete(null);
    }

    @Test
    public void deleteOrderItem_lastOrderItem_orderShouldBeDeleted() throws Exception {
        doNothing().when(orderItemService).delete(new OrderItemPK());
        when(orderService.get(anyLong())).thenReturn(order);

        // one order item in order
        order.setOrderItems(List.of(orderItem));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/order-items")
                .content(new ObjectMapper().writeValueAsString(orderItemForm))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        verify(orderService, times(1)).delete(null);
    }


}
