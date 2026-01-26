package com.example.polybookbe.service.impl;

import com.example.polybookbe.entity.Review;
import com.example.polybookbe.repository.ReviewRepository;
import com.example.polybookbe.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getByBook(Integer bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }
}
