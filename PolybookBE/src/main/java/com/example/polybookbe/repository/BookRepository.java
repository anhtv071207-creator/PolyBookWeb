package com.example.polybookbe.repository;

import com.example.polybookbe.dto.BookHomeDTO;
import com.example.polybookbe.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("""
SELECT new com.example.polybookbe.dto.BookHomeDTO(
    b.id,
    b.tieuDe,
    b.gia,
    (SELECT bi.url FROM BookImage bi WHERE bi.book.id = b.id AND bi.biaSach = true),
    0,
    b.gia
)
FROM Book b
""")
    Page<BookHomeDTO> findBooksForHome(Pageable pageable);

    Page<Book> findAll(Pageable pageable);

    @Query("""
                SELECT b FROM Book b
                JOIN b.bookCategories bc
                WHERE bc.category.id = :categoryId
            """)
    Page<Book> findByCategoryId(
            @Param("categoryId") Integer categoryId,
            Pageable pageable
    );

    //    @Query("""
//    SELECT DISTINCT b FROM Book b
//    JOIN b.bookCategories bc
//    WHERE bc.category.id IN :categoryIds
//""")
//    Page<Book> findByCategoryIdIn(
//            @Param("categoryIds") List<Integer> categoryIds,
//            Pageable pageable
//    );
    @Query("""
                SELECT b FROM Book b
                JOIN b.bookCategories bc
                WHERE bc.category.id IN :categoryIds
            """)
    Page<Book> findByCategoryIdIn(
            @Param("categoryIds") List<Integer> categoryIds,
            Pageable pageable
    );

    Page<Book> findByTieuDeContainingIgnoreCase(String keyword, Pageable pageable);

}


