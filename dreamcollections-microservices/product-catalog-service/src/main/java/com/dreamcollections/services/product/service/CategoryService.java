package com.dreamcollections.services.product.service;

import com.dreamcollections.services.product.dto.CategoryDto;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);
    Optional<CategoryDto> getCategoryById(Long id);
    Optional<CategoryDto> getCategoryByName(String name);
    List<CategoryDto> getAllCategories();
    CategoryDto updateCategory(Long id, CategoryDto categoryDto);
    void deleteCategory(Long id); // Consider implications on products
    boolean categoryExists(String name);
}
