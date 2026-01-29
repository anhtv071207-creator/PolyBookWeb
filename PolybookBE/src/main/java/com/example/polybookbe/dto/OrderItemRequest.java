package com.example.polybookbe.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Integer bookId;
    private Integer soLuong;
}
