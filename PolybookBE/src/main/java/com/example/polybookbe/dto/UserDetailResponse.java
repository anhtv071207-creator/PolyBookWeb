package com.example.polybookbe.dto;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailResponse {
    private Integer id;
    private String email;
    private String hoTen;
    private String phone;
    private String role;
    private Boolean trangThai;
    private Date ngayTao;

    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChi;
}

