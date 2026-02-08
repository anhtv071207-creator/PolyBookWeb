package com.example.polybookbe.dto;

import lombok.Data;
import java.math.BigDecimal;
@Data
public class CartItemResponse {

    private Integer bookId;
    private String tieuDe;
    private String imageUrl;
    private BigDecimal gia;
    private Integer soLuong;
    private BigDecimal tongTien;

}

