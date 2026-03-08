package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Order;
import com.example.polybookbe.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    boolean existsByOrder(Order order);
    @Query("""
    SELECT COALESCE(SUM(p.soTien), 0)
    FROM Payment p
    WHERE p.trangThai = true
""")
    BigDecimal getTotalRevenue();
}
