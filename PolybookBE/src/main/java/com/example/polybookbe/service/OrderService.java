package com.example.polybookbe.service;

import com.example.polybookbe.dto.CreateOrderRequest;
import com.example.polybookbe.dto.OrderDetailResponse;
import com.example.polybookbe.dto.OrderListResponse;
import com.example.polybookbe.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> getOrdersByUser(Integer user);

    Order createOrder(CreateOrderRequest request);

    List<OrderListResponse> getAllForManagement();

    OrderDetailResponse getDetail(Integer id);

    void updateTrangThai(Integer id, Integer trangThai);

    void cancelOrder(Integer id);

    public void deleteOrder(Integer id);

    OrderDetailResponse getDetailByMaDonHang(String maDonHang);

    List<OrderListResponse> findByEmailOrPhone(String keyword);

}
