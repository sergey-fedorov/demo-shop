package com.demo.shop.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message){
        super(message);
    }

}
