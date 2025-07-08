package com.dreamcollections.services.product.service;

import com.dreamcollections.services.product.dto.CategoryDto;
import com.dreamcollections.services.product.dto.CategoryResponseDto; // Assuming this DTO will be created
import com.dreamcollections.services.product.dto.request.CategoryRequestDto;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryDto createCategory(CategoryRequestDto categoryRequestDto); // Can be enhanced to support parentId
    Optional<CategoryResponseDto> getCategoryById(Long id); // Return type changed to support hierarchy
    Optional<CategoryDto> getCategoryByName(String name); // Might need to consider hierarchy
    List<CategoryResponseDto> getAllCategories(); // Return type changed, could be just top-level
    CategoryDto updateCategory(Long id, CategoryDto categoryDto); // Can be enhanced
    void deleteCategory(Long id); // Consider implications on products and sub-categories
    boolean categoryExists(String name);

    // New methods for hierarchical categories
    List<CategoryResponseDto> getTopLevelCategories();
    List<CategoryResponseDto> getSubCategories(Long parentCategoryId);
}
