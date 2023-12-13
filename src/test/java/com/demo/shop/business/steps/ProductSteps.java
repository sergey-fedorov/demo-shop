package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.business.models.ProductModel;
import com.demo.shop.core.BaseApi;

public class ProductSteps extends BaseApi {

    public ProductModel when_createProduct(ProductModel order){
        httpRequest.post(Endpoints.Products.PRODUCTS, order);
        return getResponseAs(ProductModel.class);
    }

}
