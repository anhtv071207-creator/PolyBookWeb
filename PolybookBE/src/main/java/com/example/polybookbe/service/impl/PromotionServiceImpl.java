package com.example.polybookbe.service.impl;

import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.Promotion;
import com.example.polybookbe.repository.BookRepository;
import com.example.polybookbe.repository.PromotionRepository;
import com.example.polybookbe.service.PromotionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final BookRepository bookRepository;


    public PromotionServiceImpl(PromotionRepository promotionRepository,
                                BookRepository bookRepository) {
        this.promotionRepository = promotionRepository;
        this.bookRepository = bookRepository;
    }

    private boolean isPromotionValid(Promotion promotion) {

        if (promotion == null || !promotion.getActive()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (promotion.getBatDau() != null &&
                now.isBefore(promotion.getBatDau())) {
            return false;
        }

        if (promotion.getKetThuc() != null &&
                now.isAfter(promotion.getKetThuc())) {
            return false;
        }

        return true;
    }

    @Override
    public int getDiscountPercent(Integer bookId, LocalDateTime now) {

        Optional<Promotion> optional =
                promotionRepository.findActivePromotionByBookId(bookId, now);

        if (optional.isEmpty()) {
            return 0;
        }

        Promotion promotion = optional.get();

        if (!isPromotionValid(promotion)) {
            return 0;
        }

        return promotion.getChietKhau();
    }

    @Override
    public BigDecimal calculateDiscountPrice(Book book) {

        if (book == null) {
            return BigDecimal.ZERO;
        }

        System.out.println("Book ID: " + book.getId());

        Optional<Promotion> optional =
                promotionRepository.findActivePromotionByBookId(
                        book.getId(),
                        LocalDateTime.now()
                );

        System.out.println("Promotion found: " + optional.isPresent());

        if (optional.isEmpty()) {
            return book.getGia();
        }

        Promotion promotion = optional.get();

        if (!isPromotionValid(promotion)) {
            return book.getGia();
        }

        int percent = promotion.getChietKhau();

        BigDecimal discount = BigDecimal.valueOf(percent)
                .divide(BigDecimal.valueOf(100));

        BigDecimal finalPrice = book.getGia()
                .multiply(BigDecimal.ONE.subtract(discount));

        System.out.println("Original price: " + book.getGia());
        System.out.println("Discount percent: " + percent);
        System.out.println("Final price: " + finalPrice);

        return finalPrice;
    }

    @Override
    public BigDecimal getDiscountPrice(Integer bookId) {

        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (bookOptional.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Book book = bookOptional.get();

        return calculateDiscountPrice(book);
    }

    @Override
    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }
    public Optional<Promotion> findById(Integer id) {
        return promotionRepository.findById(id);
    }
    @Override
    public Optional<Promotion> findByBookId(Integer bookId, LocalDateTime now) {

        Optional<Promotion> optional =
                promotionRepository.findActivePromotionByBookId(bookId, now);

        if (optional.isEmpty()) {
            return Optional.empty();
        }

        Promotion promotion = optional.get();

        if (!isPromotionValid(promotion)) {
            return Optional.empty();
        }

        return Optional.of(promotion);
    }

    @Override
    public Promotion update(Promotion promotion) {
        return promotionRepository.save(promotion);
    }
    @Override
    public void deleteById(Integer id) {
        promotionRepository.deleteById(id);
    }
    @Override
    public List<Promotion> findAll() {
        return promotionRepository.findAll();
    }
    @Override
    public Optional<Promotion> getActivePromotionByBookId(Integer bookId) {
        return promotionRepository.findActivePromotionByBookId(
                bookId,
                LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        );
    }
}