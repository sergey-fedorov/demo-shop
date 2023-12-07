package com.demo.shop.business.models;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;

@Data @Accessors(chain = true) @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerModel {

    private Long id;
    private String fullName;
    private String address;
    private String email;

    public CustomerModel getFake(){
        Faker faker = new Faker();
        return CustomerModel.builder()
                .fullName(faker.name().fullName())
                .address(faker.address().fullAddress())
                .email(faker.internet().emailAddress())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerModel that = (CustomerModel) o;
        return fullName.equals(that.fullName) && address.equals(that.address) && email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, address, email);
    }

}
