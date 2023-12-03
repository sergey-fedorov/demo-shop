package com.demo.shop.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message){
        super(message);
    }

}
