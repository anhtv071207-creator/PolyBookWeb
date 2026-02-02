package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByUserId(Integer user);
    Optional<Order> findByMaDonHang(String maDonHang);
        @Query("""
        SELECT o FROM Order o
        WHERE o.email = :keyword
           OR o.phone = :keyword
    """)
        List<Order> findByEmailOrPhone(@Param("keyword") String keyword);
}
