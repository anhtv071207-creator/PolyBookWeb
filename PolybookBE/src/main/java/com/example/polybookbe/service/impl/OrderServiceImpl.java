package com.example.polybookbe.service.impl;

import com.example.polybookbe.dto.*;
import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.Order;
import com.example.polybookbe.entity.OrderItem;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.repository.BookRepository;
import com.example.polybookbe.repository.OrderItemRepository;
import com.example.polybookbe.repository.OrderRepository;
import com.example.polybookbe.repository.UserRepository;
import com.example.polybookbe.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Order> getOrdersByUser(Integer user) {
        return orderRepository.findByUserId(user);
    }

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        Order order = new Order();

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId()).orElse(null);
            order.setUser(user);
        }

        order.setMaDonHang("DH" + System.currentTimeMillis());
        order.setTrangThai(0);
        order.setHoTenNguoiNhan(request.getHoTenNguoiNhan());
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setQuocGia(request.getQuocGia());
        order.setTinhThanh(request.getTinhThanh());
        order.setQuanHuyen(request.getQuanHuyen());
        order.setPhuongXa(request.getPhuongXa());
        order.setDiaChiNhanHang(request.getDiaChiNhanHang());
        order.setThoiGianNhan(new Date());

        BigDecimal tongTien = BigDecimal.ZERO;

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            Book book = bookRepository.findById(itemReq.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setBook(book);
            item.setSoLuong(itemReq.getSoLuong());
            item.setDonGia(book.getGia());

            tongTien = tongTien.add(
                    book.getGia().multiply(BigDecimal.valueOf(itemReq.getSoLuong()))
            );

            items.add(item);
        }

        orderItemRepository.saveAll(items);

        savedOrder.setTongTien(tongTien);
        savedOrder.setItems(items);

        return orderRepository.save(savedOrder);
    }

    @Override
    public void updateTrangThai(Integer orderId, Integer newTrangThai) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Integer current = order.getTrangThai();

        if (newTrangThai == 5) {
            if (current > 1) {
                throw new RuntimeException("Không thể hủy đơn ở trạng thái hiện tại");
            }
            order.setTrangThai(5);
            orderRepository.save(order);
            return;
        }

        if (newTrangThai != current + 1) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        order.setTrangThai(newTrangThai);
        orderRepository.save(order);
    }

    @Override
    public List<OrderListResponse> getAllForManagement() {
        return orderRepository.findAll()
                .stream()
                .map(OrderListResponse::new)
                .toList();
    }

    @Override
    public OrderDetailResponse getDetail(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderDetailResponse res = new OrderDetailResponse();

        res.setId(order.getId());
        res.setTrangThai(order.getTrangThai());

        res.setTongTien(
                order.getTongTien() != null
                        ? order.getTongTien()
                        : BigDecimal.ZERO
        );
        res.setHoTenNguoiNhan(order.getHoTenNguoiNhan());
        res.setEmail(order.getEmail());
        res.setPhone(order.getPhone());

        res.setQuocGia(order.getQuocGia());
        res.setTinhThanh(order.getTinhThanh());
        res.setQuanHuyen(order.getQuanHuyen());
        res.setPhuongXa(order.getPhuongXa());
        res.setDiaChiNhanHang(order.getDiaChiNhanHang());

        if (order.getNgayTao() != null) {
            res.setNgayTao(
                    order.getNgayTao()
                            .toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime()
            );
        }

        if (order.getUser() != null) {
            res.setUserId(order.getUser().getId());
        }

        res.setItems(
                order.getItems()
                        .stream()
                        .map(OrderItemResponse::new)
                        .toList()
        );

        return res;
    }

    @Override
    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }


    @Override
    public void cancelOrder(Integer id) {
        updateTrangThai(id, 5);
    }
}
