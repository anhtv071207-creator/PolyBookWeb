package com.example.polybookbe.controller;

import com.example.polybookbe.dto.PromotionRequest;
import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.Promotion;
import com.example.polybookbe.service.BookService;
import com.example.polybookbe.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;
    private final BookService bookService;

    @GetMapping
    public List<Promotion> getAllPromotions() {
        return promotionService.findAll();
    }

    @GetMapping("/discount/{bookId}")
    public int getDiscount(@PathVariable Integer bookId) {
        return promotionService.getDiscountPercent(bookId, LocalDateTime.now());
    }

    @GetMapping("/price/{bookId}")
    public BigDecimal getDiscountPrice(@PathVariable Integer bookId) {
        return promotionService.getDiscountPrice(bookId);
    }

    @GetMapping("/{bookId}")
    public Promotion getPromotion(@PathVariable Integer bookId) {

        return promotionService
                .findByBookId(bookId, LocalDateTime.now())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Promotion not found"
                        ));
    }

    @PostMapping
    public Promotion createPromotion(@RequestBody PromotionRequest req) {

        if (promotionService.findByBookId(req.getBookId(), LocalDateTime.now()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Promotion already exists for this book"
            );
        }

        Book book = bookService.getBookEntityById(req.getBookId());

        Promotion promotion = new Promotion();
        promotion.setBook(book);
        promotion.setChietKhau(req.getChietKhau());
        promotion.setActive(req.getActive());
        promotion.setBatDau(req.getBatDau());
        promotion.setKetThuc(req.getKetThuc());

        return promotionService.save(promotion);
    }

    @PutMapping("/{id}")
    public Promotion updatePromotion(
            @PathVariable Integer id,
            @RequestBody PromotionRequest req
    ) {

        Promotion promotion = promotionService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Promotion not found"
                ));

        promotion.setChietKhau(req.getChietKhau());
        promotion.setActive(req.getActive());
        promotion.setBatDau(req.getBatDau());
        promotion.setKetThuc(req.getKetThuc());

        return promotionService.update(promotion);
    }

    @DeleteMapping("/{id}")
    public void deletePromotion(@PathVariable Integer id) {

        if (promotionService.findById(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Promotion not found"
            );
        }

        promotionService.deleteById(id);
    }
}