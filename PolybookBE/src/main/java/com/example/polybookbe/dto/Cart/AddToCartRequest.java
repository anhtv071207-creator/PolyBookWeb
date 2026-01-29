package com.example.polybookbe.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Integer bookId;
    private Integer quantity;
    private String sessionId; // dùng cho guest
}
