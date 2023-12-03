package com.demo.shop.service;

import com.demo.shop.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentService {

    public long proceed(String type){
        if(type.equals("card")){
            return new Random().nextLong(1, 999999);
        } else
            throw new BadRequestException("Payment failed");
    }

}
