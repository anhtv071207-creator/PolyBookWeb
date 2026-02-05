package com.example.polybookbe.controller;

import com.example.polybookbe.dto.CreateOrderRequest;
import com.example.polybookbe.dto.OrderDetailResponse;
import com.example.polybookbe.dto.OrderListResponse;
import com.example.polybookbe.entity.Order;
import com.example.polybookbe.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<OrderListResponse> lookup(@RequestParam String keyword) {
        return orderService.findByEmailOrPhone(keyword);
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getDetail(@PathVariable Integer id) {
        return orderService.getDetail(id);
    }
}
