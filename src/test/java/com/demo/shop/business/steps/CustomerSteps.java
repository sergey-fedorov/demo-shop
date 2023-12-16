package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.business.models.CustomerModel;
import com.demo.shop.core.BaseApi;

import java.util.List;

import static com.demo.shop.core.Utils.getRandomElement;

public class CustomerSteps extends BaseApi {

    public CustomerModel when_createCustomer(CustomerModel customerModel){
        httpRequest.post(Endpoints.Customers.CUSTOMERS, customerModel);
        return getResponseAs(CustomerModel.class);
    }

    public CustomerModel when_getCustomerDetails(long customerId){
        httpRequest.getWithPathParams(Endpoints.Customers.CUSTOMER_BY_ID, "id", customerId);
        return getResponseAs(CustomerModel.class);
    }

    public List<CustomerModel> when_getAllCustomers(){
        httpRequest.get(Endpoints.Customers.CUSTOMERS);
        return List.of(getResponseAs(CustomerModel[].class));
    }

    public CustomerModel when_getAnyCustomer(){
        List<CustomerModel> customers = when_getAllCustomers();
        return customers.isEmpty() ?
                when_createCustomer(CustomerModel.getFake()) :
                customers.get(getRandomElement(customers));
    }
}
