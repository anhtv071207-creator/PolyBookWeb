package com.example.polybookbe.controller;

import com.example.polybookbe.entity.Cart;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.service.CartService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/user/{userId}")
    public Cart getCartByUser(@PathVariable User user) {
        return cartService.getCartByUser(user);
    }

    @GetMapping("/session/{sessionId}")
    public Cart getCartBySession(@PathVariable String sessionId) {
        return cartService.getCartBySessionId(sessionId);
    }
}
