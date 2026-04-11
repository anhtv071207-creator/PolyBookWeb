package com.example.polybookbe.service;

import com.example.polybookbe.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PaymentService {

    List<Payment> getByDateRange(LocalDateTime start, LocalDateTime end);

    List<Payment> getByMonth(int month, int year);

    List<Map<String, Object>> getRevenueByDate(LocalDateTime start, LocalDateTime end);

    List<Map<String, Object>> getRevenueByMonth(int year);

    BigDecimal getTotalRevenue();
}