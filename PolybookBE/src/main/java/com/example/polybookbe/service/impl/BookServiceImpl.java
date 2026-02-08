package com.example.polybookbe.service.impl;

import com.example.polybookbe.dto.CreateBookRequest;
import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.BookImage;
import com.example.polybookbe.repository.BookImageRepository;
import com.example.polybookbe.repository.BookRepository;
import com.example.polybookbe.service.BookService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookImageRepository bookImageRepository;
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElse(null);
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteById(Integer id) {
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Book createBook(CreateBookRequest request) {

        Book book = new Book();
        book.setTieuDe(request.getTieuDe());
        book.setTacGia(request.getTacGia());
        book.setIsbn(request.getIsbn());
        book.setGia(request.getGia());
        book.setHangTon(request.getHangTon());
        book.setMoTa(request.getMoTa());

        book.setAvgRating(BigDecimal.ZERO);
        book.setTotalReviews(0);

        Book savedBook = bookRepository.save(book);

        BookImage cover = new BookImage();
        cover.setBook(savedBook);
        cover.setUrl(request.getCoverImageUrl());
        cover.setBiaSach(true);

        bookImageRepository.save(cover);

        return savedBook;
    }
}