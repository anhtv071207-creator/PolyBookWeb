package com.example.polybookbe.repository;

import com.example.polybookbe.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookImageRepository extends JpaRepository<BookImage,Integer> {
    List<BookImage> findByBookId(Integer bookId);

    BookImage findByBookIdAndBiaSachTrue(Integer id);
}
