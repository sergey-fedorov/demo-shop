package com.demo.shop.dto;

import lombok.Data;

@Data
public class EmailValidatorDto {

    private boolean format;
    private String domain;
    private boolean disposable;
    private boolean dns;

}
