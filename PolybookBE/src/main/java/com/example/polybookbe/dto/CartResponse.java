package com.example.polybookbe.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class CartResponse {
    private Integer cartId;
    private Integer userId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private Integer totalItems;

}

