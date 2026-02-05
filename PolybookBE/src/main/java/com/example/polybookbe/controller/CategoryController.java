package com.example.polybookbe.controller;

import com.example.polybookbe.entity.Category;
import com.example.polybookbe.repository.CategoryRepository;
import com.example.polybookbe.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/root")
    public List<Category> getRootCategories() {
        return categoryService.getRootCategories();
    }

    @GetMapping("/parent/{id}")
    public List<Category> getByParent(@PathVariable Integer id) {
        return categoryService.getByParentId(id);
    }
}
