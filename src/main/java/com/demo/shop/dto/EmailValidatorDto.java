package com.demo.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class EmailValidatorDto {

    private boolean format;
    private String domain;
    private boolean disposable;
    private boolean dns;

}
