package com.example.polybookbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ma_don_hang", length = 50)
    private String maDonHang;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "tong_tien", precision = 10, scale = 2)
    private BigDecimal tongTien;

    @ManyToOne
    @JoinColumn(name = "dia_chi_nhan")
    private Address diaChiNhan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "thoi_gian_nhan")
    private Date thoiGianNhan;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

}
