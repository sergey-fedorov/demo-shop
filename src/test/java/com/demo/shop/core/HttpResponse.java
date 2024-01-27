package com.demo.shop.core;

import io.restassured.response.Response;

public class HttpResponse {

    private static final ThreadLocal<Response> response = new ThreadLocal<>();

    public static void set(Response resp){
        response.set(resp);
    }

    public static Response get(){
        return response.get();
    }
}
