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
select new com.example.polybookbe.dto.BookHomeDTO(
    b.id,
    b.tieuDe,
    b.gia,
    i.url,
    cast(coalesce(p.chietKhau,0) as integer),
    case
        when p.chietKhau is not null
        then (b.gia * (100 - p.chietKhau)) / 100
        else b.gia
    end
)
from Book b
left join b.images i
left join Promotion p
    on p.book.id = b.id
    and p.active = true
where i.biaSach = true
""")
    List<BookHomeDTO> findBooksForHome();

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

}


