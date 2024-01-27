package com.demo.shop.core;

import io.restassured.response.Response;

public class BaseApi {

    protected HttpRequest httpRequest;

    public BaseApi(){
        httpRequest = HttpRequest.getInstance();
    }

    public Response getResponse(){
        return HttpResponse.get();
    }

    public <T> T getResponseAs(Class<T> asClass){
        return getResponse().as(asClass);
    }
}
