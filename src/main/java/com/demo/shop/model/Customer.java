package com.demo.shop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;

@Entity @Table(name = "customers") @Data @Accessors(chain = true)
@Builder @NoArgsConstructor @AllArgsConstructor
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Address is required")
    private String address;


    @Column(unique = true) @NotBlank(message = "Email is required")
    private String email;


    @JsonIgnore @OneToMany(mappedBy = "customer")
    List<Order> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return fullName.equals(customer.fullName) && address.equals(customer.address) && email.equals(customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, address, email);
    }
}
