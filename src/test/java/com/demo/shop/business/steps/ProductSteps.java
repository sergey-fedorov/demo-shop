package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.business.models.ProductModel;
import com.demo.shop.core.BaseApi;
import io.qameta.allure.Step;

import java.util.List;

import static com.demo.shop.core.Utils.getRandomElement;

public class ProductSteps extends BaseApi {

    @Step
    public ProductModel when_createProduct(ProductModel order){
        httpRequest.post(Endpoints.Products.PRODUCTS, order);
        return getResponseAs(ProductModel.class);
    }

    @Step
    public List<ProductModel> when_getAllProducts(){
        httpRequest.get(Endpoints.Products.PRODUCTS);
        return List.of(getResponseAs(ProductModel[].class));
    }

    @Step
    public ProductModel when_getAnyProduct(){
        List<ProductModel> products = when_getAllProducts();
        if (products.isEmpty())
            return when_createProduct(ProductModel.getFake());
        else
            return products.get(getRandomElement(products));
    }

    @Step
    public List<ProductModel> when_getAnyProducts(int numberOfProducts){
        while (when_getAllProducts().size() < numberOfProducts)
            when_createProduct(ProductModel.getFake());

        List<ProductModel> products = when_getAllProducts();
        return products.subList(products.size() - numberOfProducts, products.size());
    }

    @Step
    public ProductModel when_getProductById(long id){
        httpRequest.getWithPathParams(Endpoints.Products.PRODUCT_BY_ID, "id", id);
        return getResponseAs(ProductModel.class);
    }

}
