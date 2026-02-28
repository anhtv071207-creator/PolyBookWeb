package com.example.polybookbe.service;

import com.example.polybookbe.dto.*;
import com.example.polybookbe.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    List<BookResponse> getAllBooks();

    BookResponse getBookById(Integer id);

    BookResponse createBook(CreateBookRequest request);

    BookResponse save(Book book);

    void deleteById(Integer id);

    BookResponse updateBook(Integer id, CreateBookRequest request);

    BookDetailResponse getDetail(Integer id);

    List<BookHomeDTO> getBooksForHome();

    PageResponse<BookResponse> getBooksWithPaging(int page, int size, String sortBy, String direction);

    PageResponse<BookResponse> getBooksByCategory (Integer categoryId, int page,int size);

}
