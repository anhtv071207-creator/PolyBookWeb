package com.example.polybookbe.service;

import com.example.polybookbe.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> getOrdersByUser(Integer userId);
}
