package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.business.models.OrderModel;
import com.demo.shop.core.BaseApi;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OrderSteps extends BaseApi {

    public OrderModel when_createOrder(OrderModel order){
        httpRequest.post(Endpoints.Orders.ORDERS, order);
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

    public OrderModel when_updateStatus(Long orderId){
        httpRequest.post(Endpoints.Orders.STATUS,  Map.of("orderId", orderId));
        return getResponseAs(OrderModel.class);
    }

}
