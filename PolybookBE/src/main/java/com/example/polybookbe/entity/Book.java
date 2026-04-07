package com.example.polybookbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "tac_gia", length = 255)
    private String tacGia;

    @Column(name = "ISBN", length = 255)
    private String isbn;

    @Lob
    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "gia", precision = 10, scale = 2)
    private BigDecimal gia;

    @Column(name = "hang_ton")
    private Integer hangTon;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating;

    @Column(name = "total_reviews")
    private Integer totalReviews;
    @Column(name = "so_trang", nullable = false)
    private Integer soTrang;

    @Column(name = "nxb", nullable = false, length = 255)
    private String nxb;

    @Column(name = "nam_xuat_ban", nullable = false)
    private Integer namXuatBan;

    @Column(name = "kich_thuoc", nullable = false, length = 255)
    private String kichThuoc;

    @Column(name = "hinh_thuc", nullable = false, length = 50)
    private String hinhThuc;

    @Column(name = "ngon_ngu", length = 50)
    private String ngonNgu;

    @Column(name = "dich_gia", length = 255)
    private String dichGia;

    @Column(name = "trong_luong", nullable = false)
    private Integer trongLuong;

    @Column(name = "ten_nha_cung_cap", nullable = false, length = 255)
    private String tenNhaCungCap;
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<BookImage> images;

    @JsonIgnore
    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BookCategory> bookCategories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Promotion> promotion;
}