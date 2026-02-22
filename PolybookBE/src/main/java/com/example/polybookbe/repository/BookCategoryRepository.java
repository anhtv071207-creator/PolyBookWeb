package com.example.polybookbe.repository;

import com.example.polybookbe.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Integer> {
    @Modifying
    @Query("DELETE FROM BookCategory bc WHERE bc.book.id = :bookId")
    void deleteByBookId(Integer bookId);
}
