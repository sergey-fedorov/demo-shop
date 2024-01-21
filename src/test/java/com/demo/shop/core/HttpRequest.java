package com.demo.shop.core;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class HttpRequest {

    private RequestSpecification getRequestSpecification(){
        return RestAssured.given().spec(RequestSpecificationFactory.getBaseRequestSpecification());
    }

    private static final ThreadLocal<Response> response = new ThreadLocal<>();

    private void setResponse(Response resp){
        response.set(resp);
    }

    public Response getResponse(){
        return response.get();
    }


    /* Custom HTTP methods */

    public Response post(String url, Object body) {
        RequestSpecification requestSpec = getRequestSpecification()
                .body(body);
        return doPost(url, requestSpec);
    }

    public Response sendPostWithPathParamAndFormData(String url, Map<String, ?> pathParams, Map<String, String> formData) {
        RequestSpecification requestSpec = getRequestSpecification()
                .contentType(ContentType.URLENC)
                .pathParams(pathParams)
                .formParams(formData);
        return doPost(url, requestSpec);
    }

    public Response get(String url) {
        return doGet(url, getRequestSpecification());
    }

    public Response getWithPathParams(String url, String paramName, Object paramValue) {
        RequestSpecification requestSpec = getRequestSpecification()
                .pathParam(paramName, paramValue);
        return doGet(url, requestSpec);
    }

    public Response getWithQueryParams(String url, Map<String, Object> queryParams) {
        RequestSpecification requestSpec = getRequestSpecification()
                .queryParams(queryParams);
        return doGet(url, requestSpec);
    }

    public Response sendPutWithPathParams(String url, Object body, String paramName, Object paramValue) {
        RequestSpecification requestSpec = getRequestSpecification()
                .body(body)
                .pathParam(paramName, paramValue);
        return doPut(url, requestSpec);
    }

    public Response sendDelete(String url, String paramName, Object paramValue) {
        RequestSpecification requestSpec = getRequestSpecification()
                .pathParam(paramName, paramValue);
        return doDelete(url, requestSpec);
    }

    /* Base HTTP methods */

    @Step("GET {url}")
    private Response doGet(String url, RequestSpecification requestSpecification) {
        Response response = requestSpecification
                .get(url)
                .then()
                .extract().response();
        setResponse(response);
        return response;
    }

    @Step("POST {url}")
    private Response doPost(String url, RequestSpecification requestSpecification) {
        Response response = requestSpecification
                .post(url)
                .then()
                .extract().response();
        setResponse(response);
        return response;
    }

    @Step("PUT {url}")
    private Response doPut(String url, RequestSpecification requestSpecification) {
        Response response = requestSpecification
                .put(url)
                .then()
                .extract().response();
        setResponse(response);
        return response;
    }

    @Step("DELETE {url}")
    private Response doDelete(String url, RequestSpecification requestSpecification) {
        Response response = requestSpecification
                .delete(url)
                .then()
                .extract().response();
        setResponse(response);
        return response;
    }

}
