package com.example.polybookbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 50)
    private String type;

    @Column(name = "ho_ten", length = 255)
    private String hoTen;

    @Column(length = 50)
    private String phone;

    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Column(name = "thanh_pho", length = 100)
    private String thanhPho;

    @Column(name = "quoc_gia", length = 100)
    private String quocGia;
}
