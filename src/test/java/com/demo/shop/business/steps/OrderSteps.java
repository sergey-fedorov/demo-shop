package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.business.models.OrderItemModel;
import com.demo.shop.business.models.OrderModel;
import com.demo.shop.business.models.PaymentModel;
import com.demo.shop.business.models.ProductModel;
import com.demo.shop.core.BaseApi;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.demo.shop.core.Utils.*;

public class OrderSteps extends BaseApi {

    CustomerSteps customerSteps = new CustomerSteps();
    ProductSteps productSteps = new ProductSteps();

    public OrderModel when_createOrder(OrderModel order){
        httpRequest.post(Endpoints.Orders.ORDERS, order);
        return getResponseAs(OrderModel.class);
    }

    public OrderModel when_createOrder(Long customerId, List<OrderItemModel> orderItems){
        OrderModel orderModelReq = OrderModel.builder().customerId(customerId).orderItems(orderItems).build();
        httpRequest.post(Endpoints.Orders.ORDERS, orderModelReq);
        return getResponseAs(OrderModel.class);
    }

    public OrderModel when_getOrder(Long orderId){
        httpRequest.getWithPathParams(Endpoints.Orders.ORDER_BY_ID, "id", orderId);
        return getResponseAs(OrderModel.class);
    }

    public List<OrderModel> when_getOrderList(){
        httpRequest.get(Endpoints.Orders.ORDERS);
        return Arrays.asList(getResponseAs(OrderModel[].class));
    }

    public List<OrderModel> when_getOrderListByStatus(String status){
        httpRequest.getWithQueryParams(Endpoints.Orders.ORDERS, Map.of("status", status));
        return Arrays.asList(getResponseAs(OrderModel[].class));
    }

    private OrderModel when_updateStatus(Long orderId){
        httpRequest.post(Endpoints.Orders.STATUS,  Map.of("orderId", orderId));
        return getResponseAs(OrderModel.class);
    }

    public Transaction when_pay(PaymentModel paymentModel){
        httpRequest.post(Endpoints.Orders.PAY, paymentModel);
        return getResponseAs(Transaction.class);
    }

    public OrderModel when_getAnyNewOrder(){
        List<OrderModel> orderList = when_getOrderListByStatus("NEW");
        if (orderList.isEmpty()){
            Long customerId = customerSteps.when_getAnyCustomer().getId();
            return when_createOrder(customerId, generateOrderItemsList(3));
        } else
            return orderList.get(getRandomElement(orderList));
    }

    private List<OrderItemModel> generateOrderItemsList(int size){
        List<OrderItemModel> orderItems = new ArrayList<>();
        int numberOfItems = getRandomPositiveIntWithLimit(size);
        List<ProductModel> products = productSteps.when_getAnyProducts(numberOfItems);

        for (int i = 0; i < numberOfItems; i++) {
            int quantity = getRandomPositiveIntWithLimit(10);
            Long productId = products.get(i).getId();
            orderItems.add(new OrderItemModel(quantity, productId));
        }
        return orderItems;
    }

    @Data
    public static class Transaction {
        private String transactionId;
    }

}
