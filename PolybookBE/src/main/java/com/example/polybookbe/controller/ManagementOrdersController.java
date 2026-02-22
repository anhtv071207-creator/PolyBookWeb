package com.example.polybookbe.controller;

import com.example.polybookbe.dto.OrderDetailResponse;
import com.example.polybookbe.dto.OrderListResponse;
import com.example.polybookbe.dto.UpdateOrderStatusRequest;
import com.example.polybookbe.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/management/orders")
@RequiredArgsConstructor
public class ManagementOrdersController {

    private final OrderService orderService;

    @GetMapping
    public Page<OrderListResponse> getAll(Pageable pageable) {
        return orderService.getAllForManagement(pageable);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
