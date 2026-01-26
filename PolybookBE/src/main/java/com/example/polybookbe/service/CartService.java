package com.example.polybookbe.service;

import com.example.polybookbe.entity.Cart;
import com.example.polybookbe.entity.User;

public interface CartService {
    Cart getCartByUser(User user);
    Cart getCartBySessionId(String sessionId);
}

