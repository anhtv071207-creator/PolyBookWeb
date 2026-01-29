package com.example.polybookbe.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Integer userId; // null nếu guest
    private String hoTenNguoiNhan;
    private String email;
    private String phone;
    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiNhanHang;

    private List<OrderItemRequest> items;
}
