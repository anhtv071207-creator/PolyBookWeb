package com.example.polybookbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String email;
    private String hoTen;
    private String phone;
    private String role;
    private Boolean trangThai;
    private Date ngayTao;
}

