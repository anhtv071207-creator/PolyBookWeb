package com.example.polybookbe.service.impl;

import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.Review;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.repository.BookRepository;
import com.example.polybookbe.repository.ReviewRepository;
import com.example.polybookbe.service.ReviewService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
    }

    // ✅ Lấy review theo book (mới nhất lên đầu)
    @Override
    public List<Review> getByBook(Integer bookId) {
        return reviewRepository.findByBookIdOrderByIdDesc(bookId);
    }

    // ✅ Thêm review (không ghi đè)
    @Override
    public Review addOrUpdate(Integer userId, Integer bookId, Integer rating, String noiDung) {

        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("Rating phải từ 1 đến 5");
        }

        // 👉 lấy book thật từ DB
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book không tồn tại"));

        // 👉 tạo user (chỉ cần id)
        User user = new User();
        user.setId(userId);

        // 👉 luôn tạo mới (không update)
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(rating);
        review.setNoiDung(noiDung);

        Review saved = reviewRepository.save(review);

        // 👉 update lại rating của book
        updateBookRating(bookId);

        return saved;
    }

    // ✅ Thống kê số lượng sao
    public Map<Integer, Integer> buildRatingStats(Integer bookId) {
        List<Object[]> list = reviewRepository.getRatingStats(bookId);

        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            map.put(i, 0);
        }

        for (Object[] row : list) {
            Integer star = (Integer) row[0];
            Long count = (Long) row[1];
            map.put(star, count.intValue());
        }

        return map;
    }

    private void updateBookRating(Integer bookId) {

        List<Review> reviews = reviewRepository.findByBookId(bookId);

        int total = reviews.size();

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);

        Book book = bookRepository.findById(bookId).orElseThrow();

        book.setAvgRating(
                BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP)
        );
        book.setTotalReviews(total);

        bookRepository.saveAndFlush(book); // 🔥 QUAN TRỌNG
    }
}