package com.example.polybookbe.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateBookRequest {

    @NotBlank
    private String tieuDe;

    @NotBlank
    private String tacGia;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal gia;

    @NotNull
    @Min(0)
    private Integer hangTon;

    private String moTa;

    @NotBlank
    private String coverImageUrl;

}
