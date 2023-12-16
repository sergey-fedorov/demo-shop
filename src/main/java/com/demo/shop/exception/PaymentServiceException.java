package com.demo.shop.exception;

public class PaymentServiceException extends RuntimeException{

    public PaymentServiceException (String message){
        super(message);
    }
}