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

    @NotBlank
    private String isbn;

    @NotNull
    @Min(0)
    private Integer hangTon;

    private String moTa;

    @NotBlank
    private String coverImageUrl;

    @NotEmpty(message = "Phải chọn ít nhất 1 danh mục")
    private List<Integer> categoryIds;

    // ====== THÊM MỚI (NOT NULL) ======

    @NotNull
    @Min(1)
    private Integer soTrang;

    @NotBlank
    private String nxb;

    @NotNull
    private Integer namXuatBan;

    @NotBlank
    private String kichThuoc;

    @NotBlank
    private String hinhThuc;

    @NotNull
    @Min(0)
    private Integer trongLuong;

    @NotBlank
    private String tenNhaCungCap;

    // ====== NULL (optional) ======

    private String ngonNgu;

    private String dichGia;
}