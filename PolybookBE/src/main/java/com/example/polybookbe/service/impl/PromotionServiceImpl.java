package com.example.polybookbe.service.impl;

import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.Promotion;
import com.example.polybookbe.exception.ApiException;
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

        if (bookId == null || bookId <= 0) {
            throw new ApiException("ID sách không hợp lệ", "INVALID_BOOK_ID");
        }

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

        if (book == null || book.getGia() == null) {
            throw new ApiException("Sách không hợp lệ", "INVALID_BOOK");
        }

        Optional<Promotion> optional =
                promotionRepository.findActivePromotionByBookId(
                        book.getId(),
                        LocalDateTime.now()
                );

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

        return book.getGia()
                .multiply(BigDecimal.ONE.subtract(discount));
    }

    @Override
    public BigDecimal getDiscountPrice(Integer bookId) {

        if (bookId == null || bookId <= 0) {
            throw new ApiException("ID sách không hợp lệ", "INVALID_BOOK_ID");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ApiException("Không tìm thấy sách", "BOOK_NOT_FOUND")
                );

        return calculateDiscountPrice(book);
    }

    @Override
    public Promotion save(Promotion promotion) {

        validatePromotion(promotion);

        return promotionRepository.save(promotion);
    }    public Optional<Promotion> findById(Integer id) {
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

        if (promotion.getId() == null) {
            throw new ApiException("Thiếu ID", "ID_REQUIRED");
        }

        Promotion existing = promotionRepository.findById(promotion.getId())
                .orElseThrow(() ->
                        new ApiException("Không tìm thấy khuyến mãi", "PROMOTION_NOT_FOUND")
                );

        validatePromotion(promotion);

        return promotionRepository.save(promotion);
    }
    public void deleteById(Integer id) {

        if (id == null || id <= 0) {
            throw new ApiException("ID không hợp lệ", "INVALID_ID");
        }

        if (!promotionRepository.existsById(id)) {
            throw new ApiException("Không tìm thấy khuyến mãi", "PROMOTION_NOT_FOUND");
        }

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
    private void validatePromotion(Promotion promotion) {

        if (promotion == null) {
            throw new ApiException("Dữ liệu không hợp lệ", "INVALID_PROMOTION");
        }

        // ===== BOOK =====
        if (promotion.getBook() == null || promotion.getBook().getId() == null) {
            throw new ApiException("Sách không hợp lệ", "BOOK_REQUIRED");
        }

        if (!bookRepository.existsById(promotion.getBook().getId())) {
            throw new ApiException("Sách không tồn tại", "BOOK_NOT_FOUND");
        }

        // ===== DISCOUNT =====
        if (promotion.getChietKhau() == null) {
            throw new ApiException("Thiếu phần trăm giảm giá", "DISCOUNT_REQUIRED");
        }

        if (promotion.getChietKhau() <= 0 || promotion.getChietKhau() > 100) {
            throw new ApiException("Phần trăm giảm giá phải từ 1-100", "INVALID_DISCOUNT");
        }

        // ===== TIME =====
        if (promotion.getBatDau() == null) {
            throw new ApiException("Thiếu thời gian bắt đầu", "START_REQUIRED");
        }

        if (promotion.getKetThuc() == null) {
            throw new ApiException("Thiếu thời gian kết thúc", "END_REQUIRED");
        }

        if (promotion.getBatDau().isAfter(promotion.getKetThuc())) {
            throw new ApiException("Thời gian không hợp lệ", "INVALID_TIME_RANGE");
        }

        // ===== ACTIVE =====
        if (promotion.getActive() == null) {
            throw new ApiException("Trạng thái không hợp lệ", "INVALID_STATUS");
        }

        // ===== OVERLAP (quan trọng) =====
        List<Promotion> list = promotionRepository
                .findByBookId(promotion.getBook().getId());

        for (Promotion p : list) {

            if (promotion.getId() != null && p.getId().equals(promotion.getId())) {
                continue;
            }

            boolean overlap =
                    !(promotion.getKetThuc().isBefore(p.getBatDau())
                            || promotion.getBatDau().isAfter(p.getKetThuc()));

            if (overlap) {
                throw new ApiException(
                        "Đã tồn tại khuyến mãi trùng thời gian",
                        "PROMOTION_OVERLAP"
                );
            }
        }
    }
}