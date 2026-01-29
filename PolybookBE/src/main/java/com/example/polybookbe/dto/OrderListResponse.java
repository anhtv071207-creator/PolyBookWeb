package com.example.polybookbe.dto;

import com.example.polybookbe.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderListResponse {

    private Integer id;              // mã đơn
    private String maDonHang;      // DH1769...
    private Integer trangThai;      // CHO_XAC_NHAN
    private String tenKhachHang;   // Drake
    private BigDecimal tongTien;       // 1023000
    private LocalDateTime ngayTao; // createdAt

    // hiển thị gọn sản phẩm
    private String sanPhamTomTat;  // "3 sản phẩm"
    public OrderListResponse(Order order) {
        this.id = order.getId();
        this.maDonHang = order.getMaDonHang();
        this.trangThai = order.getTrangThai();
        this.tongTien = order.getTongTien();
    }
}
