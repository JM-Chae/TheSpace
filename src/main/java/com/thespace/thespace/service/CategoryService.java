package com.thespace.thespace.service;

import com.thespace.thespace.dto.CategoryDTO;

import java.util.List;

public interface CategoryService
  {
    Long createCategory(CategoryDTO categoryDTO);
    List<CategoryDTO> getAllCategories(String path);
    void deleteCategory(Long categoryId);
  }
