package com.example.polybookbe.controller;

import com.example.polybookbe.dto.BookDetailResponse;
import com.example.polybookbe.dto.BookHomeDTO;
import com.example.polybookbe.entity.Book;
import com.example.polybookbe.entity.BookImage;
import com.example.polybookbe.repository.BookImageRepository;
import com.example.polybookbe.repository.BookRepository;
import com.example.polybookbe.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.save(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Integer id) {
        bookService.deleteById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> getDetail(@PathVariable Integer id) {

        Book book = bookRepository.findById(id)
                .orElseThrow();

        List<BookImage> images = imageRepo.findByBookId(id);

        BookDetailResponse res = new BookDetailResponse();
        res.setId(book.getId());
        res.setTieuDe(book.getTieuDe());
        res.setTacGia(book.getTacGia());
        res.setGia(book.getGia());

        res.setMainImage(
                images.stream()
                        .filter(i -> Boolean.TRUE.equals(i.getBiaSach()))
                        .map(BookImage::getUrl)
                        .findFirst()
                        .orElse("/books/default.jpg")
        );

        res.setThumbnails(
                images.stream()
                        .map(BookImage::getUrl)
                        .toList()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/home")
    public List<BookHomeDTO> home() {
        return bookRepository.findBooksForHome();
    }
}
