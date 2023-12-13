package com.demo.shop.business.models;

import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductModel {

    private Long id;
    private String name;
    private Double price;
    private String pictureUrl;

    public static ProductModel getFake(){
        Faker faker = new Faker();
        return ProductModel.builder()
                .name(faker.commerce().productName())
                .price(faker.number().randomDouble(1, 1, 100))
                .pictureUrl(faker.internet().image())
                .build();
    }

}
