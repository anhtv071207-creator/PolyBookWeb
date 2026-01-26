package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}

