package com.dreamcollections.services.product.service.impl;

import com.dreamcollections.services.product.dto.CategoryDto;
import com.dreamcollections.services.product.exception.ResourceConflictException;
import com.dreamcollections.services.product.exception.ResourceNotFoundException;
import com.dreamcollections.services.product.model.Category;
import com.dreamcollections.services.product.repository.CategoryRepository;
import com.dreamcollections.services.product.repository.ProductRepository; // To check for products before deleting category
import com.dreamcollections.services.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;


    private CategoryDto convertToDto(Category category) {
        if (category == null) return null;
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }

    private Category convertToEntity(CategoryDto categoryDto) {
        if (categoryDto == null) return null;
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        return category;
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ResourceConflictException("Category with name '" + categoryDto.getName() + "' already exists.");
        }
        Category category = convertToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDto> getCategoryById(Long id) {
        return categoryRepository.findById(id).map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDto> getCategoryByName(String name) {
        return categoryRepository.findByName(name).map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        Optional<Category> existingCategoryWithName = categoryRepository.findByName(categoryDto.getName());
        if (existingCategoryWithName.isPresent() && !existingCategoryWithName.get().getId().equals(id)) {
            throw new ResourceConflictException("Category name '" + categoryDto.getName() + "' is already used by another category.");
        }

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        Category updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if any products are associated with this category
        if (productRepository.findByCategoryId(id, Pageable.ofSize(1)).hasContent()) {
            throw new ResourceConflictException("Cannot delete category: It has associated products. Please reassign or delete products first.");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean categoryExists(String name) {
        return categoryRepository.existsByName(name);
    }
}
