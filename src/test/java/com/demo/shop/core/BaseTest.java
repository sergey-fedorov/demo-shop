package com.demo.shop.core;

import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    public static void setup(){
        RequestSpecificationFactory.setBaseUri(System.getProperty("base.uri"));
    }

}
