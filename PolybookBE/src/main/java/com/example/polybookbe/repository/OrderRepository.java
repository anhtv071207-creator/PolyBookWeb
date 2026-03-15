package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByUserId(Integer userId);
    Optional<Order> findByMaDonHang(String maDonHang);
        @Query("""
        SELECT o FROM Order o
        WHERE o.email = :keyword
           OR o.phone = :keyword
    """)
        List<Order> findByEmailOrPhone(@Param("keyword") String keyword);
    Page<Order> findByUserId(Integer userId, Pageable pageable);

    Page<Order> findByEmailContainingOrPhoneContaining(
            String email,
            String phone,
            Pageable pageable
    );
    @Query("""
       SELECT COALESCE(SUM(o.tongTien), 0)
       FROM Order o
       WHERE o.trangThai = :status
       """)
    BigDecimal sumTongTienByTrangThai(Integer status);

    @Query("""
SELECT o FROM Order o
ORDER BY
CASE
    WHEN o.trangThai IN (4,5,6) THEN 1
    ELSE 0
END,
o.ngayTao DESC
""")
    Page<Order> findAllForManagement(Pageable pageable);
}
