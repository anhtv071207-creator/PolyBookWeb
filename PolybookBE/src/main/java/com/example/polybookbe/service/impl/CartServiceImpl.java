package com.example.polybookbe.service.impl;

import com.example.polybookbe.entity.Cart;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.repository.CartRepository;
import com.example.polybookbe.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * User đã đăng nhập
     */
    @Override
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    /**
     * Guest (chưa đăng nhập)
     */
    @Override
    public Cart getCartBySessionId(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setSessionId(sessionId);
                    return cartRepository.save(cart);
                });
    }
}
