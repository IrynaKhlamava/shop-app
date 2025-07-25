package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private String userId;

    private List<CartItemDto> items;

    private int totalQuantity;

    private BigDecimal totalPrice;

    private List<MissingProductDto> missingProducts;

}

