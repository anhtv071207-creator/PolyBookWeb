package com.example.polybookbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "so_tien", precision = 10, scale = 2)
    private BigDecimal soTien;

    @Column(name = "phuong_thuc", length = 50)
    private String phuongThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "user_id", nullable = true)
    private Integer userId;

    @Column(name = "ma_giao_dich")
    private String maGiaoDich;
}
