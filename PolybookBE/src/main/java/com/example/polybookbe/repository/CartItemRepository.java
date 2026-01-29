package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.Cart;
import com.example.polybookbe.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartAndBook(Cart cart, Book book);

}
