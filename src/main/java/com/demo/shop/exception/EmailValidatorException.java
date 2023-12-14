package com.demo.shop.exception;

public class EmailValidatorException extends RuntimeException {

    public EmailValidatorException(String message){
        super(message);
    }
}
