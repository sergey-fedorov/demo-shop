package com.demo.shop.core;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HeaderConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;

public abstract class RequestSpecificationFactory {

    private static String baseUri = PropertiesReader.getProperty("local.address");
    private static final int DEFAULT_PORT = Integer.parseInt(PropertiesReader.getProperty("server.port"));

    private static RequestSpecification requestSpecification;

    public static void setBaseUri(String baseUri) {
        if (baseUri != null)
            RequestSpecificationFactory.baseUri = baseUri;
    }

    public static RequestSpecification getBaseRequestSpecification(){
        if (requestSpecification == null){
            requestSpecification =  new RequestSpecBuilder()
                    .setConfig(getRestAssuredConfig())
                    .addFilter(new AllureRestAssured())
                    .addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter())
                    .setContentType(ContentType.JSON)
                    .setBaseUri(baseUri)
                    .setPort(DEFAULT_PORT)
                    .addHeader("accept", "application/json")
                    .build();
        }
        return requestSpecification;
    }

    private static RestAssuredConfig getRestAssuredConfig(){
        return RestAssuredConfig.config()
                .headerConfig(HeaderConfig.headerConfig()
                        .overwriteHeadersWithName("Content-Type"))
                .objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.GSON));
    }

    public static void mock(String baseUri, int port){
        getBaseRequestSpecification().port(port).baseUri(baseUri);
    }

    public static void unMock(){
        getBaseRequestSpecification().port(DEFAULT_PORT).baseUri(baseUri);
    }

}
