package com.example.lamashop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String street;

    private String city;

    private String state;

    private String zipCode;

    private String country;
}
