package com.example.polybookbe.controller;

import com.example.polybookbe.dto.Order.OrderDetailResponse;
import com.example.polybookbe.dto.Order.OrderListResponse;
import com.example.polybookbe.dto.Order.UpdateOrderStatusRequest;
import com.example.polybookbe.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/management/orders")
@RequiredArgsConstructor
public class ManagementOrdersController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<OrderListResponse> getAll() {
        return orderService.getAllForManagement();
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getDetail(@PathVariable Integer id) {
        return orderService.getDetail(id);
    }

    @PutMapping("/{id}/status")
    public void updateStatus(
            @PathVariable Integer id,
            @RequestBody UpdateOrderStatusRequest req
    ) {
        orderService.updateTrangThai(id, req.getTrangThai());
    }

    @PutMapping("/{id}/cancel")
    public void cancel(@PathVariable Integer id) {
        orderService.cancelOrder(id);
    }

    @GetMapping("/management/orders/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getDetail(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

}
