package com.dreamcollections.services.product.controller;

import com.dreamcollections.services.product.dto.CategoryDto;
import com.dreamcollections.services.product.dto.CategoryResponseDto;
import com.dreamcollections.services.product.dto.request.CategoryRequestDto;
import com.dreamcollections.services.product.payload.response.MessageResponse;
import com.dreamcollections.services.product.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        log.info("Request to create category: Name: {}, ParentId: {}", categoryRequestDto.getName(), categoryRequestDto.getParentId());
        CategoryDto createdCategory = categoryService.createCategory(categoryRequestDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        log.debug("Request to get category by ID: {}", id);
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name) {
        log.debug("Request to get category by name: {}", name);
        // This returns a simple DTO, which is fine for name lookup.
        // If full hierarchy is needed by name, a new service/controller method would be better.
        return categoryService.getCategoryByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gets all top-level categories along with their sub-categories.
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllTopLevelCategoriesWithChildren() {
        log.debug("Request to get all top-level categories with their children");
        List<CategoryResponseDto> categories = categoryService.getTopLevelCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Explicit endpoint to get only top-level categories (can include their children based on DTO structure).
     * This is functionally similar to the plain GET /categories endpoint now.
     */
    @GetMapping("/toplevel")
    public ResponseEntity<List<CategoryResponseDto>> getTopLevelCategories() {
        log.debug("Request to get top-level categories");
        List<CategoryResponseDto> categories = categoryService.getTopLevelCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Gets sub-categories for a given parent category ID.
     */
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategories(@PathVariable Long parentId) {
        log.debug("Request to get sub-categories for parent ID: {}", parentId);
        List<CategoryResponseDto> subCategories = categoryService.getSubCategories(parentId);
        if (subCategories.isEmpty() && !categoryService.getCategoryById(parentId).isPresent()) {
            // Optional: Check if parent actually exists to return 404 if parent is not found,
            // otherwise an empty list is fine if parent exists but has no children.
            // The service method getSubCategories already throws if parent not found.
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subCategories);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Request to update category ID {}: Name: {}, ParentId: {}", id, categoryDto.getName(), categoryDto.getParentId());
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable Long id) {
        log.info("Request to delete category ID: {}", id);
        categoryService.deleteCategory(id);
        // Ensure MessageResponse is available or create a simple one.
        // For now, assuming it's in: com.dreamcollections.services.product.payload.response.MessageResponse
        return ResponseEntity.ok(new MessageResponse("Category deleted successfully!"));
    }
}
