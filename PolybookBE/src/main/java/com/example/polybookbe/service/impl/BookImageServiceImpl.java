package com.example.polybookbe.service.impl;
import com.example.polybookbe.entity.BookImage;
import com.example.polybookbe.repository.BookImageRepository;
import com.example.polybookbe.service.BookImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookImageServiceImpl implements BookImageService {

    private final BookImageRepository bookImageRepository;

    @Override
    @Transactional
    public void deleteByBookId(Integer bookId) {
        bookImageRepository.deleteByBookId(bookId);
    }

    @Override
    @Transactional
    public BookImage save(BookImage bookImage) {
        return bookImageRepository.save(bookImage);
    }

    @Override
    public BookImage findCoverByBookId(Integer bookId) {
        return bookImageRepository.findByBookIdAndBiaSachTrue(bookId);
    }
    @Override
    public List<BookImage> findByBookId(Integer bookId) {
        return bookImageRepository.findByBookId(bookId);
    }

}