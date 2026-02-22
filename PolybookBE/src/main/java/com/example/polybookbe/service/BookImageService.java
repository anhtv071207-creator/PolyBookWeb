package com.example.polybookbe.service;

import com.example.polybookbe.entity.BookImage;

import java.util.List;

public interface BookImageService {
    void deleteByBookId(Integer bookId);
    BookImage save(BookImage bookImage);
    BookImage findCoverByBookId(Integer bookId);
    List<BookImage> findByBookId(Integer bookId);

}
