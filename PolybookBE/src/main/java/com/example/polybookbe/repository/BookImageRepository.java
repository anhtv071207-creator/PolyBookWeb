package com.example.polybookbe.repository;

import com.example.polybookbe.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImageRepository extends JpaRepository<BookImage,Integer> {
}
