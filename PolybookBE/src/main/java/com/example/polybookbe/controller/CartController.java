package com.example.polybookbe.controller;

import com.example.polybookbe.dto.AddToCartRequest;
import com.example.polybookbe.dto.CartResponse;
import com.example.polybookbe.dto.OrderItemRequest;
import com.example.polybookbe.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @GetMapping("/user/{userId}")
    public CartResponse getCartByUser(@PathVariable Integer userId) {
        return cartService.getCartByUser(userId);
    }


    @PostMapping("/add")
    public CartResponse addToCart(
            @RequestParam Integer userId,
            @RequestBody AddToCartRequest request
    ) {
        return cartService.addToCart(userId, request);
    }

    @DeleteMapping("/items/{bookId}")
    public CartResponse removeItem(
            @RequestParam Integer userId,
            @PathVariable Integer bookId
    ) {
        return cartService.removeItem(userId, bookId);
    }

    @GetMapping("/checkout-items")
    public List<OrderItemRequest> getCheckoutItems(
            @RequestParam Integer userId
    ) {
        return cartService.getCheckoutItems(userId);
    }
}
