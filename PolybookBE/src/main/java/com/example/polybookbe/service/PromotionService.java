package com.example.polybookbe.service;

import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.Promotion;
import lombok.Setter;
import org.springframework.stereotype.Service;

import com.example.polybookbe.entity.Book;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionService {

    int getDiscountPercent(Integer bookId, LocalDateTime now);

    BigDecimal calculateDiscountPrice(Book book);

    BigDecimal getDiscountPrice(Integer bookId);

    Promotion save(Promotion promotion);

    Optional<Promotion> findByBookId(Integer bookId, LocalDateTime now);

    Promotion update(Promotion promotion);

    List<Promotion> findAll();

    void deleteById(Integer bookId);

    Optional<Promotion> getActivePromotionByBookId(Integer bookId);

    Optional <Promotion> findById(Integer id);
}
