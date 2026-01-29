package com.example.polybookbe.dto.Order;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Integer bookId;
    private Integer soLuong;
}
