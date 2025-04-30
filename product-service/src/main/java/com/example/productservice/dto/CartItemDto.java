package com.example.productservice.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private String productId;
    private int quantity;
}
