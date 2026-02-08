package com.example.polybookbe.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String hoTen;
    private String email;
    private String phone;
    private String password;
}
