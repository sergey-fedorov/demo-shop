package com.demo.shop.core;

public class BaseApi {

    protected HttpRequest httpRequest;

    public BaseApi(){
        httpRequest = new HttpRequest();
    }

    public <T> T getResponseAs(Class<T> asClass){
        return httpRequest.getResponse().as(asClass);
    }
}
