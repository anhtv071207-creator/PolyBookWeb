package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    List<Category> findByDanhMucChaIsNull();

    List<Category> findByDanhMucCha_Id(Integer parentId);
}
