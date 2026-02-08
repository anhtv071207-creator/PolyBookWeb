package com.example.polybookbe.service.impl;

import com.example.polybookbe.dto.*;
import com.example.polybookbe.entity.*;
import com.example.polybookbe.repository.*;
import com.example.polybookbe.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal SHIPPING_FEE = BigDecimal.valueOf(10000);

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;

    @Override
    public List<Order> getOrdersByUser(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        Order order = new Order();

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));
            order.setUser(user);
        }

        order.setMaDonHang("DH-" + UUID.randomUUID().toString().substring(0, 8));
        order.setTrangThai(0);
        fillShippingInfo(order, request);

        BigDecimal tongTien = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        if (user == null) {
            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new RuntimeException("Danh sách sản phẩm trống");
            }

            for (OrderItemRequest req : request.getItems()) {

                if (req.getSoLuong() <= 0) {
                    throw new RuntimeException("Số lượng không hợp lệ");
                }

                Book book = bookRepository.findById(req.getBookId())
                        .orElseThrow(() -> new RuntimeException("Book not found"));

                if (book.getHangTon() < req.getSoLuong()) {
                    throw new RuntimeException("Không đủ tồn kho");
                }

                book.setHangTon(book.getHangTon() - req.getSoLuong());

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setBook(book);
                item.setSoLuong(req.getSoLuong());
                item.setDonGia(book.getGia());

                tongTien = tongTien.add(
                        book.getGia().multiply(BigDecimal.valueOf(req.getSoLuong()))
                );

                items.add(item);
            }

        } else {

            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Cart không tồn tại"));

            List<CartItem> cartItems = cartItemRepository.findByCart(cart);

            if (cartItems.isEmpty()) {
                throw new RuntimeException("Cart trống");
            }

            for (CartItem cartItem : cartItems) {

                if (cartItem.getSoLuong() <= 0) {
                    throw new RuntimeException("Số lượng không hợp lệ");
                }

                Book book = cartItem.getBook();

                if (book.getHangTon() < cartItem.getSoLuong()) {
                    throw new RuntimeException("Không đủ tồn kho");
                }

                book.setHangTon(book.getHangTon() - cartItem.getSoLuong());

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setBook(book);
                item.setSoLuong(cartItem.getSoLuong());
                item.setDonGia(cartItem.getDonGia());

                tongTien = tongTien.add(
                        cartItem.getDonGia()
                                .multiply(BigDecimal.valueOf(cartItem.getSoLuong()))
                );

                items.add(item);
            }

            cartItemRepository.deleteAll(cartItems);
        }

        order.setTongTien(tongTien.add(SHIPPING_FEE));
        order.setItems(items);

        orderRepository.save(order);
        orderItemRepository.saveAll(items);

        return order;
    }

    @Override
    @Transactional
    public void updateTrangThai(Integer orderId, Integer newTrangThai) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Integer current = order.getTrangThai();

        if (current == 5) {
            throw new RuntimeException("Đơn đã bị hủy");
        }

        if (newTrangThai == 5) {
            if (current >= 2) {
                throw new RuntimeException("Không thể hủy đơn ở trạng thái hiện tại");
            }

            for (OrderItem item : order.getItems()) {
                Book book = item.getBook();
                book.setHangTon(book.getHangTon() + item.getSoLuong());
            }

            order.setTrangThai(5);
            return;
        }

        if (!newTrangThai.equals(current + 1)) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        order.setTrangThai(newTrangThai);
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
        return buildOrderDetailResponse(order);
    }

    @Override
    public OrderDetailResponse getDetailByMaDonHang(String maDonHang) {
        Order order = orderRepository.findByMaDonHang(maDonHang)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return buildOrderDetailResponse(order);
    }

    @Override
    public List<OrderListResponse> findByEmailOrPhone(String keyword) {
        return orderRepository.findByEmailOrPhone(keyword)
                .stream()
                .map(OrderListResponse::new)
                .toList();
    }

    @Override
    @Transactional
    public void deleteOrder(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            book.setHangTon(book.getHangTon() + item.getSoLuong());
        }

        orderItemRepository.deleteAll(order.getItems());
        orderRepository.delete(order);
    }

    @Override
    public void cancelOrder(Integer id) {
        updateTrangThai(id, 5);
    }

    private void fillShippingInfo(Order order, CreateOrderRequest request) {
        order.setHoTenNguoiNhan(request.getHoTenNguoiNhan());
        order.setEmail(request.getEmail());
        order.setPhone(request.getPhone());
        order.setQuocGia(request.getQuocGia());
        order.setTinhThanh(request.getTinhThanh());
        order.setQuanHuyen(request.getQuanHuyen());
        order.setPhuongXa(request.getPhuongXa());
        order.setDiaChiNhanHang(request.getDiaChiNhanHang());
    }

    private OrderDetailResponse buildOrderDetailResponse(Order order) {

        OrderDetailResponse res = new OrderDetailResponse();

        res.setId(order.getId());
        res.setTrangThai(order.getTrangThai());
        res.setTongTien(order.getTongTien() != null ? order.getTongTien() : BigDecimal.ZERO);

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
}
