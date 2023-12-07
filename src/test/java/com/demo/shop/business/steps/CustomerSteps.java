package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.business.models.CustomerModel;
import com.demo.shop.core.BaseApi;

public class CustomerSteps extends BaseApi {

    public CustomerModel when_createCustomer(CustomerModel customerModel){
        httpRequest.post(Endpoints.Customers.CUSTOMERS, customerModel);
        return getResponseAs(CustomerModel.class);
    }

    public CustomerModel when_getCustomerDetails(long customerId){
        httpRequest.getWithPathParams(Endpoints.Customers.CUSTOMER_BY_ID, "id", customerId);
        return getResponseAs(CustomerModel.class);
    }




}
