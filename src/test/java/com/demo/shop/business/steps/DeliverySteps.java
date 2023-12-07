package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.core.BaseApi;

import java.util.Map;

public class DeliverySteps extends BaseApi {

    public void when_deliver(Long orderId){
        httpRequest.post(Endpoints.Delivery.COMPLETE, Map.of("orderId", orderId));
    }

    public void when_getPaidOrders(){
        httpRequest.get(Endpoints.Delivery.PAID_ORDERS);
    }


}
