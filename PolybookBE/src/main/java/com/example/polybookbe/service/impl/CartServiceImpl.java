package com.example.polybookbe.service.impl;

import com.example.polybookbe.dto.Cart.AddToCartRequest;
import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.Cart;
import com.example.polybookbe.entity.CartItem;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.repository.BookRepository;
import com.example.polybookbe.repository.CartItemRepository;
import com.example.polybookbe.repository.CartRepository;
import com.example.polybookbe.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            BookRepository bookRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Cart getCartBySessionId(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setSessionId(sessionId);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Cart addToCart(User user, AddToCartRequest request) {

        Cart cart = (user != null)
                ? getCartByUser(user)
                : getCartBySessionId(request.getSessionId());

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        CartItem item = cartItemRepository
                .findByCartAndBook(cart, book)
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setBook(book);
            item.setSoLuong(request.getQuantity());
            item.setDonGia(book.getGia());
        } else {
            item.setSoLuong(item.getSoLuong() + request.getQuantity());
        }

        cartItemRepository.save(item);
        return cart;
    }

}
