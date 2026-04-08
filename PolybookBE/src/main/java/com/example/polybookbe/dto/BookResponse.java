package com.example.polybookbe.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookResponse {

    private Integer id;
    private String tieuDe;
    private String tacGia;
    private String isbn;
    private BigDecimal gia;
    private Integer hangTon;
    private String moTa;
    private String mainImage;

    private BigDecimal avgRating;
    private Integer totalReviews;

    private Integer soTrang;
    private String nxb;
    private Integer namXuatBan;
    private String kichThuoc;
    private String hinhThuc;
    private Integer trongLuong;
    private String tenNhaCungCap;
    private String ngonNgu;
    private String dichGia;

    private List<BookImageResponse> images;
    private List<CategoryResponse> categories;
    private Integer discount;
    private BigDecimal salePrice;
}