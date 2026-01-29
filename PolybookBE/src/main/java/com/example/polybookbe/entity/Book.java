package com.example.polybookbe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<BookImage> images;

}
