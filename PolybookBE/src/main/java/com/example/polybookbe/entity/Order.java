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
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "ma_don_hang", length = 50)
    private String maDonHang;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "tong_tien", precision = 10, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "ho_ten_nguoi_nhan", length = 100)
    private String hoTenNguoiNhan;

    @Column(name = "quoc_gia", length = 100)
    private String quocGia;

    @Column(name = "tinh_thanh", length = 100)
    private String tinhThanh;

    @Column(name = "quan_huyen", length = 100)
    private String quanHuyen;

    @Column(name = "phuong_xa", length = 100)
    private String phuongXa;

    @Column(name = "dia_chi_nhan_hang", length = 255)
    private String diaChiNhanHang;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private Date ngayTao;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items;
}
