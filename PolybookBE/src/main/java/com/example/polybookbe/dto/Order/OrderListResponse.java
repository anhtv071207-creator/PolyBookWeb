package com.example.polybookbe.dto.Order;

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

    private Integer id;
    private String maDonHang;
    private Integer trangThai;
    private String hoTenNguoiNhan;
    private BigDecimal tongTien;
    private LocalDateTime ngayTao;

    private String sanPhamTomTat;
    public OrderListResponse(Order order) {
        this.id = order.getId();
        this.maDonHang = order.getMaDonHang();
        this.trangThai = order.getTrangThai();
        this.tongTien = order.getTongTien();

        this.hoTenNguoiNhan = order.getHoTenNguoiNhan();

        if (order.getNgayTao() != null) {
            this.ngayTao = order.getNgayTao()
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            this.sanPhamTomTat = order.getItems().size() + " sản phẩm";
        }
    }

}
