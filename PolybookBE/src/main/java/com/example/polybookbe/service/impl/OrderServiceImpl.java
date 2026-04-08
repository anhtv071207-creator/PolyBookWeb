package com.example.polybookbe.service.impl;

import com.example.polybookbe.dto.*;
import com.example.polybookbe.entity.*;
import com.example.polybookbe.repository.*;
import com.example.polybookbe.service.OrderService;
import com.example.polybookbe.service.PromotionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PromotionService promotionService;

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
        order.setPhuongThucThanhToan(request.getPhuongThucThanhToan());

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

                BigDecimal finalPrice = promotionService.calculateDiscountPrice(book);

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setBook(book);
                item.setSoLuong(req.getSoLuong());
                item.setDonGia(finalPrice);

                tongTien = tongTien.add(
                        finalPrice.multiply(BigDecimal.valueOf(req.getSoLuong()))
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


                BigDecimal finalPrice = promotionService.calculateDiscountPrice(book);

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setBook(book);
                item.setSoLuong(cartItem.getSoLuong());
                item.setDonGia(finalPrice);

                tongTien = tongTien.add(
                        finalPrice.multiply(BigDecimal.valueOf(cartItem.getSoLuong()))
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

        if (current == 5 || current == 6) {
            throw new RuntimeException("Đơn đã kết thúc");
        }

        if (newTrangThai < current) {
            throw new RuntimeException("Không thể quay ngược trạng thái");
        }

        // =========================
        // TRỪ TỒN KHO KHI XÁC NHẬN
        // =========================
        if (current < 2 && newTrangThai >= 2){

            for (OrderItem item : order.getItems()) {

                Book book = item.getBook();

                if (book.getHangTon() < item.getSoLuong()) {
                    throw new RuntimeException("Không đủ tồn kho");
                }

                book.setHangTon(book.getHangTon() - item.getSoLuong());
                bookRepository.save(book);
            }
        }

        // =========================
        // HỦY ĐƠN
        // =========================
        if (newTrangThai == 5) {

            if (current >= 3) {
                throw new RuntimeException("Không thể hủy ở trạng thái này");
            }

            if (current >= 2) {
                restoreStock(order);
            }

            order.setTrangThai(5);
            return;
        }

        // =========================
        // HOÀN TRẢ
        // =========================
        if (newTrangThai == 6) {

            if (current < 4) {
                throw new RuntimeException("Chỉ hoàn trả sau khi giao");
            }

            restoreStock(order);
            order.setTrangThai(6);
            return;
        }

        // =========================
        // TRẠNG THÁI LỖI
        // =========================
        if (newTrangThai == 7) {

            if (current != 3 && current != 4) {
                throw new RuntimeException("Chỉ có thể chuyển sang lỗi từ đang giao hoặc giao thành công");
            }

            order.setTrangThai(7);
            return;
        }

        order.setTrangThai(newTrangThai);

        // =========================
        // TẠO PAYMENT KHI HOÀN THÀNH
        // =========================
        if (newTrangThai == 4) {

            boolean exists = paymentRepository.existsByOrder(order);

            if (!exists) {

                Payment payment = new Payment();
                payment.setOrder(order);
                payment.setUserId(order.getUser() != null ? order.getUser().getId() : null);
                payment.setMaGiaoDich("PAY-" + UUID.randomUUID().toString().substring(0, 10));
                payment.setSoTien(order.getTongTien());
                payment.setPhuongThuc(order.getPhuongThucThanhToan());

                if ("COD".equals(order.getPhuongThucThanhToan())) {
                    payment.setTrangThai(true);
                } else {
                    payment.setTrangThai(false);
                }

                paymentRepository.save(payment);
            }
        }
    }

    private void restoreStock(Order order) {

        for (OrderItem item : order.getItems()) {

            Book book = item.getBook();

            book.setHangTon(book.getHangTon() + item.getSoLuong());

            bookRepository.save(book);
        }
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
    public Page<OrderListResponse> getAllForManagement(Pageable pageable) {

        return orderRepository.findAllForManagement(pageable)
                .map(OrderListResponse::new);
    }

    @Override
    @Transactional
    public void deleteOrder(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        for (OrderItem item : order.getItems()) {

            Book book = item.getBook();

            if (order.getTrangThai() >= 2 && order.getTrangThai() != 7) {
                book.setHangTon(book.getHangTon() + item.getSoLuong());
                bookRepository.save(book);
            }
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
                        .map(item -> {
                            var promo = promotionService.getActivePromotionByBookId(item.getBook().getId());

                            return new OrderItemResponse(
                                    item,
                                    promo.orElse(null)
                            );
                        })
                        .toList()
        );

        return res;
    }

    @Override
    public List<OrderListResponse> getOrdersForLookup(Integer userId) {

        if (userId == null) {
            return List.of();
        }

        return orderRepository.findByUserId(userId)
                .stream()
                .map(OrderListResponse::new)
                .toList();
    }

    @Override
    public Page<OrderListResponse> getOrdersByUser(Integer userId, Pageable pageable) {

        if (userId == null) {
            return Page.empty(pageable);
        }

        return orderRepository.findByUserId(userId, pageable)
                .map(OrderListResponse::new);
    }

    @Override
    public Page<OrderListResponse> searchByEmailOrPhone(
            String keyword,
            Pageable pageable
    ) {

        return orderRepository
                .findByEmailContainingOrPhoneContaining(keyword, keyword, pageable)
                .map(OrderListResponse::new);
    }

    @Override
    public BigDecimal getRevenueByStatus(Integer status) {
        return orderRepository.sumTongTienByTrangThai(status);
    }
}