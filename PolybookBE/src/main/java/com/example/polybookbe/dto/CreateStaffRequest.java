package com.example.polybookbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateStaffRequest {
    private String email;
    private String password;
    private String hoTen;
    private String phone;
}

