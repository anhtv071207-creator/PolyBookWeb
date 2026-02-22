package com.example.polybookbe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_danh_muc", nullable = false, length = 100)
    private String tenDanhMuc;

    @Column(length = 100)
    private String url;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "danh_muc_cha")
    private Category danhMucCha;

    @OneToMany(mappedBy = "danhMucCha")
    @JsonManagedReference
    private List<Category> danhMucCon;
    @OneToMany(mappedBy = "category")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<BookCategory> bookCategories;
}
