package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.business.models.PaymentModel;
import com.demo.shop.core.BaseApi;
import lombok.Data;

public class PaymentSteps extends BaseApi {

    public Transaction when_pay(PaymentModel paymentModel){
        httpRequest.post(Endpoints.Payments.PAY, paymentModel);
        return getResponseAs(Transaction.class);
    }

    @Data
    public static class Transaction {
        private String transactionId;
    }

}
