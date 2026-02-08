package com.example.polybookbe.service.impl;

import com.example.polybookbe.dto.AddToCartRequest;
import com.example.polybookbe.dto.CartItemResponse;
import com.example.polybookbe.dto.CartResponse;
import com.example.polybookbe.dto.OrderItemRequest;
import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.Cart;
import com.example.polybookbe.entity.CartItem;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.repository.BookRepository;
import com.example.polybookbe.repository.CartItemRepository;
import com.example.polybookbe.repository.CartRepository;
import com.example.polybookbe.repository.UserRepository;
import com.example.polybookbe.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // ===================== GET CART =====================
    @Override
    public CartResponse getCartByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        return mapToCartResponse(cart);
    }

    // ===================== ADD TO CART =====================
    @Override
    @Transactional
    public CartResponse addToCart(Integer userId, AddToCartRequest request) {

        if (request.getQuantity() == 0) {
            throw new IllegalStateException("Số lượng không hợp lệ");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalStateException("Sách không tồn tại"));

        CartItem item = cartItemRepository
                .findByCartAndBook(cart, book)
                .orElse(null);

        int currentQty = (item != null) ? item.getSoLuong() : 0;
        int newQty = currentQty + request.getQuantity();

        // Nếu số lượng <= 0 → xoá item
        if (newQty <= 0) {
            if (item != null) {
                cartItemRepository.delete(item);
            }
            return mapToCartResponse(cart);
        }

        // Check tồn kho
        if (newQty > book.getHangTon()) {
            throw new IllegalStateException(
                    "Sách \"" + book.getTieuDe() + "\" chỉ còn " + book.getHangTon()
            );
        }

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setBook(book);
            item.setSoLuong(newQty);
            item.setDonGia(book.getGia()); // snapshot giá tại thời điểm thêm
        } else {
            item.setSoLuong(newQty);
        }

        cartItemRepository.save(item);
        return mapToCartResponse(cart);
    }

    // ===================== REMOVE ITEM =====================
    @Override
    @Transactional
    public CartResponse removeItem(Integer userId, Integer bookId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại"));

        CartItem item = cartItemRepository
                .findByCartAndBook(cart, book)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));

        cartItemRepository.delete(item);
        return mapToCartResponse(cart);
    }

    // ===================== MAPPER =====================
    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUser().getId());

        List<CartItemResponse> items = cart.getItems().stream().map(item -> {
            CartItemResponse dto = new CartItemResponse();
            dto.setBookId(item.getBook().getId());
            dto.setTieuDe(item.getBook().getTieuDe());
            dto.setImageUrl(
                    item.getBook().getImages().isEmpty()
                            ? null
                            : item.getBook().getImages().get(0).getUrl()
            );
            dto.setGia(item.getDonGia());
            dto.setSoLuong(item.getSoLuong());
            dto.setTongTien(
                    item.getDonGia().multiply(
                            BigDecimal.valueOf(item.getSoLuong())
                    )
            );
            return dto;
        }).toList();

        response.setItems(items);
        response.setTotalItems(
                items.stream().mapToInt(CartItemResponse::getSoLuong).sum()
        );
        response.setTotalAmount(
                items.stream()
                        .map(CartItemResponse::getTongTien)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        return response;
    }
    @Override
    public List<OrderItemRequest> getCheckoutItems(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart không tồn tại"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        return cartItems.stream().map(cartItem -> {
            OrderItemRequest dto = new OrderItemRequest();
            dto.setBookId(cartItem.getBook().getId());
            dto.setSoLuong(cartItem.getSoLuong());
            return dto;
        }).toList();
    }



}
