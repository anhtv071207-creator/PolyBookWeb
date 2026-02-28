package com.example.polybookbe.repository;

import com.example.polybookbe.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    List<Category> findByDanhMucChaIsNull();

    List<Category> findByDanhMucCha_Id(Integer parentId);

    @Query("SELECT c.id FROM Category c WHERE c.danhMucCha.id = :parentId")
    List<Integer> findChildIds(@Param("parentId") Integer parentId);
}
