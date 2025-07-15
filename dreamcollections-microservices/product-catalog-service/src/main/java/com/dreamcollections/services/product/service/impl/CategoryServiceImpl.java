package com.dreamcollections.services.product.service.impl;

import com.dreamcollections.services.product.dto.CategoryDto;
import com.dreamcollections.services.product.dto.CategoryResponseDto;
import com.dreamcollections.services.product.dto.request.CategoryRequestDto;
import com.dreamcollections.services.product.exception.ResourceConflictException;
import com.dreamcollections.services.product.exception.ResourceNotFoundException;
import com.dreamcollections.services.product.model.Category;
import com.dreamcollections.services.product.repository.CategoryRepository;
import com.dreamcollections.services.product.repository.ProductRepository;
import com.dreamcollections.services.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // --- DTO Conversion Methods ---

    private CategoryDto convertToSimpleDto(Category category) {
        if (category == null) return null;
        Long parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
        return new CategoryDto(category.getId(), category.getName(), category.getDescription(), parentId);
    }

    private Category convertToEntity(CategoryRequestDto dto, CategoryRepository categoryRepository) {
        if (dto == null) return null;
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + dto.getParentId()));
            category.setParentCategory(parent);
        }
        return category;
    }


    private CategoryResponseDto convertToResponseDto(Category category) {
        return convertToResponseDto(category, true); // By default, load subcategories
    }

    private CategoryResponseDto convertToResponseDto(Category category, boolean fetchSubCategories) {
        if (category == null) return null;

        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());

        if (category.getParentCategory() != null) {
            dto.setParentId(category.getParentCategory().getId());
            dto.setParentName(category.getParentCategory().getName());
        }

        if (fetchSubCategories && category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            dto.setSubCategories(
                category.getSubCategories().stream()
                    .map(subCategory -> convertToResponseDto(subCategory, true)) // Recursively map, fetching their subcategories
                    .collect(Collectors.toList())
            );
        } else {
            dto.setSubCategories(Collections.emptyList());
        }
        return dto;
    }


    // --- Service Method Implementations ---

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRepository.existsByName(categoryRequestDto.getName())) {
            throw new ResourceConflictException("Category with name '" + categoryRequestDto.getName() + "' already exists.");
        }
        Category category = convertToEntity(categoryRequestDto, categoryRepository);
        Category savedCategory = categoryRepository.save(category);
        return convertToSimpleDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponseDto> getCategoryById(Long id) {
        // Fetch category with subcategories eagerly if needed, or rely on LAZY loading and transactional context
        // For simplicity, direct conversion. For performance, consider @EntityGraph.
        return categoryRepository.findById(id).map(category -> convertToResponseDto(category, true));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDto> getCategoryByName(String name) {
        return categoryRepository.findByName(name).map(this::convertToSimpleDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        // This will fetch all categories and then structure them.
        // For large datasets, this might be inefficient.
        // Consider fetching only top-level and loading children on demand or using a different approach.
        // For now, fetching all and converting.
        return categoryRepository.findByParentCategoryIsNull().stream() // Start with top-level
                .map(category -> convertToResponseDto(category, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getTopLevelCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream()
                .map(category -> convertToResponseDto(category, true)) // Fetch subcategories for top levels
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getSubCategories(Long parentCategoryId) {
        if (!categoryRepository.existsById(parentCategoryId)) {
            throw new ResourceNotFoundException("Parent category not found with id: " + parentCategoryId);
        }
        return categoryRepository.findByParentCategoryId(parentCategoryId).stream()
                .map(category -> convertToResponseDto(category, true)) // Fetch subcategories for these children
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check for name conflict
        Optional<Category> existingCategoryWithName = categoryRepository.findByName(categoryDto.getName());
        if (existingCategoryWithName.isPresent() && !existingCategoryWithName.get().getId().equals(id)) {
            throw new ResourceConflictException("Category name '" + categoryDto.getName() + "' is already used by another category.");
        }

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        // Handle parent update
        if (categoryDto.getParentId() != null) {
            if (categoryDto.getParentId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent.");
            }
            Category parentCategory = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + categoryDto.getParentId()));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null); // Setting as a top-level category
        }

        Category updatedCategory = categoryRepository.save(category);
        return convertToSimpleDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if any products are associated with this category
        // This check might need to be recursive if products can only be in leaf categories
        if (productRepository.findByCategoryId(id, Pageable.ofSize(1)).hasContent()) {
            throw new ResourceConflictException("Cannot delete category: '" + category.getName() + "'. It has associated products. Please reassign or delete products first.");
        }

        // If the category has sub-categories, they will be deleted due to CascadeType.ALL on subCategories.
        // If this is not desired, the cascade type should be changed and sub-categories handled manually (e.g., reassign to parent or block deletion).
        if (!category.getSubCategories().isEmpty()) {
            // Optionally, add a warning or specific logic here if subcategories exist.
            // For example, prevent deletion if subcategories exist and are not empty of products.
            // For now, relying on cascade.
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean categoryExists(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getAllCategoryIdsIncludingSubcategories(Long categoryId) {
        List<Long> categoryIds = new ArrayList<>();
        collectCategoryIdsRecursively(categoryId, categoryIds);
        return categoryIds;
    }

    private void collectCategoryIdsRecursively(Long categoryId, List<Long> categoryIds) {
        // Add the current category ID
        categoryIds.add(categoryId);

        // Find all subcategories and recursively add their IDs
        List<Category> subCategories = categoryRepository.findByParentCategoryId(categoryId);
        for (Category subCategory : subCategories) {
            collectCategoryIdsRecursively(subCategory.getId(), categoryIds);
        }
    }
}
