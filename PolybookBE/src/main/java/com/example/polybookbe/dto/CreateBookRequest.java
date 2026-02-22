package com.example.polybookbe.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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
    private String isbn;
    @NotNull
    @Min(0)
    private Integer hangTon;

    private String moTa;

    @NotBlank
    private String coverImageUrl;

    @NotEmpty(message = "Phải chọn ít nhất 1 danh mục")
    private List<Integer> categoryIds;
}
