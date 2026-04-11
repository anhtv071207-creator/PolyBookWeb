package com.example.polybookbe.service.impl;

import com.example.polybookbe.entity.Payment;
import com.example.polybookbe.repository.PaymentRepository;
import com.example.polybookbe.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<Payment> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByDateRange(start, end);
    }

    @Override
    public List<Payment> getByMonth(int month, int year) {
        return paymentRepository.findByMonth(month, year);
    }

    @Override
    public List<Map<String, Object>> getRevenueByDate(LocalDateTime start, LocalDateTime end) {
        List<Object[]> raw = paymentRepository.getRevenueByDate(start, end);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", row[0]);
            map.put("total", row[1]);
            result.add(map);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getRevenueByMonth(int year) {
        List<Object[]> raw = paymentRepository.getRevenueByMonth(year);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("total", row[1]);
            result.add(map);
        }

        return result;
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return paymentRepository.getTotalRevenue();
    }
}