package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Integer> {
    List<Review> findByBookId(Integer bookId);
    List<Review> findByBookIdOrderByIdDesc(Integer bookId);
    Review findByUserIdAndBookId(Integer userId, Integer bookId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double getAvgRating(Integer bookId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId")
    Integer getTotalReviews(Integer bookId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.book.id = :bookId GROUP BY r.rating")
    List<Object[]> getRatingStats(Integer bookId);

}
