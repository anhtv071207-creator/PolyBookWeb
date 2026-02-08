package com.example.polybookbe.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateProfileRequest {
    private String hoTen;
    private String phone;
    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChi;
}
