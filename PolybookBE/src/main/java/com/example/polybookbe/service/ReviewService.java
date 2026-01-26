package com.example.polybookbe.service;

import com.example.polybookbe.entity.Review;

import java.util.List;

public interface ReviewService {

    List<Review> getByBook(Integer bookId);

    Review save(Review review);
}
