package com.example.polybookbe.service.impl;

import com.example.polybookbe.dto.*;
import com.example.polybookbe.entity.*;
import com.example.polybookbe.repository.*;
import com.example.polybookbe.service.BookImageService;
import com.example.polybookbe.service.BookService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookImageService bookImageService;
    @Autowired
    private BookCategoryRepository bookCategoryRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Override
    public List<BookResponse> getAllBooks(){
        return bookRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public BookResponse getBookById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return mapToResponse(book);
    }


    @Override
    public BookResponse save(Book book) {
        Book saved = bookRepository.save(book);
        return mapToResponse(saved);
    }


    @Override
    public void deleteById(Integer id) {
        bookImageService.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {

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

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {

            for (Integer categoryId : request.getCategoryIds()) {

                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found"));

                if (category.getDanhMucCon() != null && !category.getDanhMucCon().isEmpty()) {
                    throw new RuntimeException("Only leaf categories can be assigned");
                }

                BookCategoryId id = new BookCategoryId(savedBook.getId(), categoryId);

                BookCategory bookCategory = new BookCategory();
                bookCategory.setId(id);
                bookCategory.setBook(savedBook);
                bookCategory.setCategory(category);

                savedBook.getBookCategories().add(bookCategory);
            }
        }

        BookImage cover = new BookImage();
        cover.setBook(savedBook);
        cover.setUrl(request.getCoverImageUrl());
        cover.setBiaSach(true);

        bookImageService.save(cover);

        return mapToResponse(savedBook);
    }


    @Override
    @Transactional
    public BookResponse updateBook(Integer id, CreateBookRequest request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setTieuDe(request.getTieuDe());
        book.setTacGia(request.getTacGia());
        book.setIsbn(request.getIsbn());
        book.setGia(request.getGia());
        book.setHangTon(request.getHangTon());
        book.setMoTa(request.getMoTa());

        List<Integer> newIds = request.getCategoryIds();

        List<Integer> oldIds = book.getBookCategories()
                .stream()
                .map(bc -> bc.getCategory().getId())
                .toList();

        boolean changed = newIds == null
                ? !oldIds.isEmpty()
                : !oldIds.containsAll(newIds) || oldIds.size() != newIds.size();

        if (changed) {

            bookCategoryRepository.deleteByBookId(id);

            if (newIds != null && !newIds.isEmpty()) {

                for (Integer categoryId : newIds) {

                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found"));

                    if (category.getDanhMucCon() != null && !category.getDanhMucCon().isEmpty()) {
                        throw new RuntimeException("Only leaf categories can be assigned");
                    }

                    BookCategoryId bookCategoryId = new BookCategoryId(id, categoryId);

                    BookCategory bookCategory = new BookCategory();
                    bookCategory.setId(bookCategoryId);
                    bookCategory.setBook(book);
                    bookCategory.setCategory(category);

                    bookCategoryRepository.save(bookCategory);
                }
            }
        }

        BookImage cover = bookImageService.findCoverByBookId(id);

        if (cover != null) {
            cover.setUrl(request.getCoverImageUrl());
            bookImageService.save(cover);
        }

        return mapToResponse(book);
    }
    private BookResponse mapToResponse(Book book) {

        BookResponse response = new BookResponse();

        response.setId(book.getId());
        response.setTieuDe(book.getTieuDe());
        response.setTacGia(book.getTacGia());
        response.setIsbn(book.getIsbn());
        response.setGia(book.getGia());
        response.setHangTon(book.getHangTon());
        response.setMoTa(book.getMoTa());
        response.setAvgRating(book.getAvgRating());
        response.setTotalReviews(book.getTotalReviews());
        Integer discount = 0;
        BigDecimal salePrice = book.getGia();

        Promotion promo = promotionRepository
                .findActivePromotionByBookId(book.getId(), LocalDateTime.now())
                .orElse(null);

        if (promo != null) {
            discount = promo.getChietKhau();

            salePrice = book.getGia()
                    .multiply(BigDecimal.valueOf(100 - discount))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        response.setDiscount(discount);
        response.setSalePrice(salePrice);
        List<BookImage> images = bookImageService.findByBookId(book.getId());

        if (images != null && !images.isEmpty()) {
            response.setImages(
                    images.stream().map(img -> {
                        BookImageResponse imgRes = new BookImageResponse();
                        imgRes.setUrl(img.getUrl());
                        imgRes.setBiaSach(img.getBiaSach());
                        return imgRes;
                    }).toList()
            );
        }

        if (book.getBookCategories() != null) {
            response.setCategories(
                    book.getBookCategories().stream().map(bc -> {
                        CategoryResponse catRes = new CategoryResponse();
                        catRes.setId(bc.getCategory().getId());
                        catRes.setTen(bc.getCategory().getTenDanhMuc());
                        return catRes;
                    }).toList()
            );
        }

        return response;
    }

    @Override
    public BookDetailResponse getDetail(Integer id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        List<BookImage> images = bookImageService.findByBookId(id);

        List<CategoryResponse> categories =
                book.getBookCategories().stream()
                        .map(bc -> {
                            CategoryResponse cat = new CategoryResponse();
                            cat.setId(bc.getCategory().getId());
                            cat.setTen(bc.getCategory().getTenDanhMuc());
                            return cat;
                        })
                        .toList();

        promotionRepository.findActivePromotionByBookId(book.getId(), LocalDateTime.now());

        Integer discount = 0;
        BigDecimal salePrice = book.getGia();

        Promotion promo = promotionRepository
                .findActivePromotionByBookId(book.getId(), LocalDateTime.now())
                .orElse(null);

        if (promo != null) {
            discount = promo.getChietKhau();

            salePrice = book.getGia()
                    .multiply(BigDecimal.valueOf(100 - discount))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        return new BookDetailResponse(
                book.getId(),
                book.getTieuDe(),
                book.getTacGia(),
                book.getGia(),
                book.getHangTon(),
                images.stream()
                        .filter(i -> Boolean.TRUE.equals(i.getBiaSach()))
                        .map(BookImage::getUrl)
                        .findFirst()
                        .orElse("/books/default.jpg"),
                images.stream()
                        .map(BookImage::getUrl)
                        .toList(),
                book.getMoTa(),
                book.getAvgRating(),
                book.getTotalReviews(),
                categories,
                discount,
                salePrice
        );
    }
    @Override
    public List<BookHomeDTO> getBooksForHome() {
        return bookRepository.findBooksForHome();
    }
    @Override
    public PageResponse<BookResponse> getBooksWithPaging(
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> bookPage = bookRepository.findAll(pageable);

        List<BookResponse> content = bookPage
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PageResponse<>(
                content,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast()
        );
    }
    @Override
    public PageResponse<BookResponse> getBooksByCategory(
            Integer categoryId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        List<Integer> childIds =
                categoryRepository.findChildIds(categoryId);

        Page<Book> bookPage;

        if (!childIds.isEmpty()) {

            bookPage = bookRepository
                    .findByCategoryIdIn(childIds, pageable);

        } else {

            bookPage = bookRepository
                    .findByCategoryId(categoryId, pageable);
        }

        List<BookResponse> content =
                bookPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return new PageResponse<>(
                content,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast()
        );
    }
    @Override
    public Book getBookEntityById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    @Override
    public PageResponse<BookResponse> searchBooks(
            String keyword,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Book> bookPage =
                bookRepository.findByTieuDeContainingIgnoreCase(keyword, pageable);

        List<BookResponse> list =
                bookPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return new PageResponse<>(
                list,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast()
        );
    }
}