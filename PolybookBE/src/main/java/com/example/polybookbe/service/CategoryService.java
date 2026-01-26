package com.example.polybookbe.service;

import com.example.polybookbe.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAll();

    Category getById(Integer id);

    Category save(Category category);
}
