package com.example.polybookbe.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromotionRequest {

    private Integer bookId;
    private Integer chietKhau;
    private LocalDateTime batDau;
    private LocalDateTime ketThuc;
    private Boolean active;

}
