package com.example.polybookbe.dto.Cart;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Integer bookId;
    private Integer quantity;
    private String sessionId; 
}
