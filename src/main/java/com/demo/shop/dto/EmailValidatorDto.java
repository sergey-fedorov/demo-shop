package com.demo.shop.dto;

import lombok.*;

@Data @Builder @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode
public class EmailValidatorDto {

    private boolean format;
    private String domain;
    private boolean disposable;
    private boolean dns;
    private boolean whitelist;

}
