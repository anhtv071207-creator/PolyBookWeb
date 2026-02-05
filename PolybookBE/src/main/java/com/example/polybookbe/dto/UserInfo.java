package com.example.polybookbe.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private Integer userId;
    private String hoTenNguoiNhan;
    private String email;
    private String phone;
    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChi;
}
