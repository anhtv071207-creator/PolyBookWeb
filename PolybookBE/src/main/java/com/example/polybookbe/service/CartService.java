package com.example.polybookbe.service;

import com.example.polybookbe.dto.AddToCartRequest;
import com.example.polybookbe.dto.CartResponse;
import com.example.polybookbe.dto.OrderItemRequest;
import com.example.polybookbe.entity.Cart;
import com.example.polybookbe.entity.User;

import java.util.List;

public interface CartService {
    CartResponse getCartByUser(Integer userId);

    CartResponse addToCart(Integer userId, AddToCartRequest request);

    CartResponse removeItem(Integer userId, Integer bookId);

    List<OrderItemRequest> getCheckoutItems(Integer userId);


}
