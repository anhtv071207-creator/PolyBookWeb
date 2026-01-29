package com.example.polybookbe.dto.Book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class BookHomeDTO {
    private Integer id;
    private String tieuDe;
    private BigDecimal gia;
    private String url;
}

