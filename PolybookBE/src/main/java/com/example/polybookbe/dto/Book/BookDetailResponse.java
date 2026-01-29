package com.example.polybookbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDetailResponse {

    private Integer id;
    private String tieuDe;
    private String tacGia;
    private BigDecimal gia;

    private String mainImage;
    private List<String> thumbnails;
}
