package com.example.polybookbe.controller;

import com.example.polybookbe.entity.Payment;
import com.example.polybookbe.repository.PaymentRepository;
import com.example.polybookbe.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/management/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // 1. Lọc theo khoảng ngày
    @GetMapping("/by-date")
    public List<Payment> getByDate(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);

        return paymentService.getByDateRange(startDate, endDate);
    }

    // 2. Lọc theo tháng
    @GetMapping("/by-month")
    public List<Payment> getByMonth(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return paymentService.getByMonth(month, year);
    }

    // 3. Doanh thu theo ngày (dashboard)
    @GetMapping("/revenue-by-date")
    public List<Map<String, Object>> revenueByDate(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);

        return paymentService.getRevenueByDate(startDate, endDate);
    }

    // 4. Doanh thu theo tháng (dashboard)
    @GetMapping("/revenue-by-month")
    public List<Map<String, Object>> revenueByMonth(
            @RequestParam int year
    ) {
        return paymentService.getRevenueByMonth(year);
    }

    // 5. Tổng doanh thu
    @GetMapping("/total-revenue")
    public BigDecimal getTotalRevenue() {
        return paymentService.getTotalRevenue();
    }
    @GetMapping("/top-quantity")
    public List<Map<String, Object>> topQuantity() {
        return paymentService.getTopBooksByQuantity();
    }

    @GetMapping("/top-revenue")
    public List<Map<String, Object>> topRevenue() {
        return paymentService.getTopBooksByRevenue();
    }
}