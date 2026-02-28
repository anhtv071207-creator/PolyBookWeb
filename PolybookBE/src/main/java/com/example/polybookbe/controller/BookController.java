package com.example.polybookbe.controller;

import com.example.polybookbe.dto.*;
import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.BookImage;
import com.example.polybookbe.repository.BookImageRepository;
import com.example.polybookbe.repository.BookRepository;
import com.example.polybookbe.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookImageRepository imageRepo;

//    @GetMapping
//    public List<BookResponse> getAllBooks() {
//        return bookService.getAllBooks();
//    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody CreateBookRequest request) {

        BookResponse createdBook = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> getDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getDetail(id));
    }

    @GetMapping("/home")
    public List<BookHomeDTO> home() {
        return bookService.getBooksForHome();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateBookRequest request) {

        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @GetMapping
    public PageResponse<BookResponse> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return bookService.getBooksWithPaging(page, size, sortBy, direction);
    }
    @GetMapping("/category/{id}")
    public PageResponse<BookResponse> getBooksByCategory(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return bookService.getBooksByCategory(id, page, size);
    }
}
