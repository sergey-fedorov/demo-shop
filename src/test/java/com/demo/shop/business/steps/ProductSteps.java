package com.demo.shop.business.steps;

import com.demo.shop.business.Endpoints;
import com.demo.shop.business.models.ProductModel;
import com.demo.shop.core.BaseApi;

import java.util.List;

import static com.demo.shop.core.Utils.getRandomElement;

public class ProductSteps extends BaseApi {

    public ProductModel when_createProduct(ProductModel order){
        httpRequest.post(Endpoints.Products.PRODUCTS, order);
        return getResponseAs(ProductModel.class);
    }

    public List<ProductModel> when_getAllProducts(){
        httpRequest.get(Endpoints.Products.PRODUCTS);
        return List.of(getResponseAs(ProductModel[].class));
    }

    public ProductModel when_getAnyProduct(){
        List<ProductModel> products = when_getAllProducts();
        if (products.isEmpty())
            return when_createProduct(ProductModel.getFake());
        else
            return products.get(getRandomElement(products));
    }

    public List<ProductModel> when_getAnyProducts(int numberOfProducts){
        List<ProductModel> products = when_getAllProducts();

        while (products.size() < numberOfProducts)
            when_createProduct(ProductModel.getFake());

        return products.subList(products.size() - numberOfProducts, products.size());
    }

}
