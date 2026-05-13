package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    @Query("""
    SELECT p FROM Promotion p
    WHERE p.book.id = :bookId
    AND p.active = true
    AND p.batDau <= :now
    AND p.ketThuc >= :now
""")
    Optional<Promotion> findActivePromotionByBookId(
            @Param("bookId") Integer bookId,
            @Param("now") LocalDateTime now
    );
    List<Promotion> findByBookId(Integer bookId);
}

