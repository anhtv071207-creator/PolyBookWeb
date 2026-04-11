package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Order;
import com.example.polybookbe.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    boolean existsByOrder(Order order);

    @Query("""
        SELECT COALESCE(SUM(p.soTien), 0)
        FROM Payment p
        WHERE p.trangThai = true
    """)
    BigDecimal getTotalRevenue();

    // Lọc theo khoảng ngày
    @Query("""
        SELECT p FROM Payment p
        WHERE p.trangThai = true
        AND p.createdAt BETWEEN :start AND :end
    """)
    List<Payment> findByDateRange(LocalDateTime start, LocalDateTime end);

    // Lọc theo tháng
    @Query("""
        SELECT p FROM Payment p
        WHERE p.trangThai = true
        AND MONTH(p.createdAt) = :month
        AND YEAR(p.createdAt) = :year
    """)
    List<Payment> findByMonth(int month, int year);

    @Query(value = """
    SELECT CAST(p.created_at AS DATE) AS ngay, SUM(p.so_tien) AS tong
    FROM payment p
    WHERE p.trang_thai = 1
    AND p.created_at BETWEEN :start AND :end
    GROUP BY CAST(p.created_at AS DATE)
    ORDER BY CAST(p.created_at AS DATE)
""", nativeQuery = true)
    List<Object[]> getRevenueByDate(LocalDateTime start, LocalDateTime end);

    // Doanh thu theo tháng
    @Query("""
        SELECT MONTH(p.createdAt), SUM(p.soTien)
        FROM Payment p
        WHERE p.trangThai = true
        AND YEAR(p.createdAt) = :year
        GROUP BY MONTH(p.createdAt)
        ORDER BY MONTH(p.createdAt)
    """)
    List<Object[]> getRevenueByMonth(int year);
}
