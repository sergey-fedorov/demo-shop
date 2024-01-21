package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.core.BaseApi;
import io.qameta.allure.Step;

import java.util.Map;

public class DeliverySteps extends BaseApi {

    @Step
    public void when_deliver(Long orderId){
        httpRequest.post(Endpoints.Delivery.COMPLETE, Map.of("orderId", orderId));
    }

    @Step
    public void when_getPaidOrders(){
        httpRequest.get(Endpoints.Delivery.PAID_ORDERS);
    }


}
