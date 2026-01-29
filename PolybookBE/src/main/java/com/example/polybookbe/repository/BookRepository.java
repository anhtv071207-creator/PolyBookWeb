package com.example.polybookbe.repository;

import com.example.polybookbe.dto.BookHomeDTO;
import com.example.polybookbe.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("""
        select new com.example.polybookbe.dto.BookHomeDTO(
            b.id,
            b.tieuDe,
            b.gia,
            i.url
        )
        from Book b
        left join b.images i
        where i.biaSach = true
    """)
    List<BookHomeDTO> findBooksForHome();
}

