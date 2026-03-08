package com.example.polybookbe.controller;

import com.example.polybookbe.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/management/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentRepository paymentRepository;
    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getRevenue() {
        BigDecimal revenue = paymentRepository.getTotalRevenue();
        return ResponseEntity.ok(revenue);
    }
}
