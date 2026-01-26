package com.example.polybookbe.service;

import com.example.polybookbe.entity.Book;

import java.util.List;

public interface BookService {

    List<Book> getAllBooks();

    Book getBookById(Integer id);

    Book save(Book book);

    void deleteById(Integer id);
}
