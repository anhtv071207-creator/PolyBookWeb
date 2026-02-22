package com.example.polybookbe.controller;

import com.example.polybookbe.dto.CreateOrderRequest;
import com.example.polybookbe.dto.OrderDetailResponse;
import com.example.polybookbe.dto.OrderListResponse;
import com.example.polybookbe.entity.Order;
import com.example.polybookbe.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest request) {

        Order order = orderService.createOrder(request);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Đặt hàng thành công",
                        "maDonHang", order.getMaDonHang()
                )
        );
    }

    @GetMapping("/lookup")
    public Page<OrderListResponse> lookup(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        if (keyword != null && !keyword.isBlank()) {
            return orderService.searchByEmailOrPhone(keyword, pageable);
        }

        if (userId != null) {
            return orderService.getOrdersByUser(userId, pageable);
        }

        return Page.empty(pageable);
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getDetail(@PathVariable Integer id) {
        return orderService.getDetail(id);
    }

    @GetMapping("/revenue")
    public BigDecimal getRevenue(
            @RequestParam(defaultValue = "4") Integer status
    ) {
        return orderService.getRevenueByStatus(status);
    }


}
