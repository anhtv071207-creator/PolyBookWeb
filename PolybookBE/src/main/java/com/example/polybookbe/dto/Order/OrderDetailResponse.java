package com.example.polybookbe.dto.Order;

import com.example.polybookbe.dto.Order.OrderItemResponse;
import com.example.polybookbe.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailResponse {

    private Integer id;

    private Integer userId;
    private String hoTenNguoiNhan;
    private String email;
    private String phone;

    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiNhanHang;

    private Integer trangThai;
    private BigDecimal tongTien;

    private LocalDateTime ngayTao;
    private LocalDateTime thoiGianDatHang;

    private List<OrderItemResponse> items;

    public OrderDetailResponse(Order order) {
        this.id = order.getId();

        this.userId = order.getUser() != null
                ? order.getUser().getId()
                : null;

        this.hoTenNguoiNhan = order.getHoTenNguoiNhan();
        this.email = order.getEmail();
        this.phone = order.getPhone();

        this.quocGia = order.getQuocGia();
        this.tinhThanh = order.getTinhThanh();
        this.quanHuyen = order.getQuanHuyen();
        this.phuongXa = order.getPhuongXa();
        this.diaChiNhanHang = order.getDiaChiNhanHang();

        this.trangThai = order.getTrangThai();
        this.tongTien = order.getTongTien();

        if (order.getNgayTao() != null) {
            LocalDateTime time = order.getNgayTao()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            this.ngayTao = time;
            this.thoiGianDatHang = time;
        }

        this.items = order.getItems()
                .stream()
                .map(OrderItemResponse::new)
                .toList();
    }
    
}
