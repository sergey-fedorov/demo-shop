package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.business.models.PaymentModel;
import com.demo.shop.core.BaseApi;
import io.qameta.allure.Step;
import lombok.Data;

public class PaymentSteps extends BaseApi {

    @Step
    public Transaction when_proceed(PaymentModel paymentModel){
        httpRequest.post(Endpoints.Payment.PROCEED, paymentModel);
        return getResponseAs(Transaction.class);
    }

    @Data
    public static class Transaction {
        private String transactionId;
    }
}
