package com.example.polybookbe.service;

import com.example.polybookbe.dto.Cart.AddToCartRequest;
import com.example.polybookbe.entity.Cart;
import com.example.polybookbe.entity.User;

public interface CartService {

    Cart getCartByUser(User user);

    Cart getCartBySessionId(String sessionId);

    Cart addToCart(User user, AddToCartRequest request);
}
