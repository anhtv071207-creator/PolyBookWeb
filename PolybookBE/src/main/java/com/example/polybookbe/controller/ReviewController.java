package com.example.polybookbe.controller;


import com.example.polybookbe.entity.Review;
import com.example.polybookbe.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ✅ 1. Lấy danh sách review theo book
    @GetMapping("/book/{bookId}")
    public List<Review> getByBook(@PathVariable Integer bookId) {
        return reviewService.getByBook(bookId);
    }

    // ✅ 2. Thêm / cập nhật review
    @PostMapping
    public Review addOrUpdate(
            @RequestParam Integer userId,
            @RequestParam Integer bookId,
            @RequestParam Integer rating,
            @RequestParam String noiDung
    ) {
        return reviewService.addOrUpdate(userId, bookId, rating, noiDung);
    }
}