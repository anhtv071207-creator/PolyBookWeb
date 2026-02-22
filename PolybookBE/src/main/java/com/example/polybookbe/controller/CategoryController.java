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
    @GetMapping("/{id}")
    public Category getById(@PathVariable Integer id) {
        return categoryService.getById(id);
    }
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryService.save(category);
    }
    @PutMapping("/{id}")
    public Category update(@PathVariable Integer id,
                           @RequestBody Category categoryRequest) {

        Category category = categoryService.getById(id);

        category.setTenDanhMuc(categoryRequest.getTenDanhMuc());

        if (categoryRequest.getDanhMucCha() != null) {
            Category parent = categoryService.getById(
                    categoryRequest.getDanhMucCha().getId()
            );
            category.setDanhMucCha(parent);
        } else {
            category.setDanhMucCha(null);
        }

        return categoryService.save(category);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        categoryService.deleteById(id);
    }
}
