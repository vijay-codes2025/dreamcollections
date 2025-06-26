package com.dreamcollections.services.product.controller;

import com.dreamcollections.services.product.dto.CategoryDto;
import com.dreamcollections.services.product.payload.response.MessageResponse; // Need this DTO
import com.dreamcollections.services.product.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Import for @PreAuthorize if security is added to this service
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600) // Handled by API Gateway ideally
@RestController
@RequestMapping("/categories") // Path within this service. API Gateway maps /api/catalog/categories here
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    // For PreAuthorize, this service would need its own Spring Security setup
    // to validate JWTs. For now, assuming endpoints are public or secured by gateway.
    // If direct calls are made to this service bypassing gateway, security is vital.

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Request to create category: {}", categoryDto.getName());
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        log.debug("Request to get category by ID: {}", id);
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name) {
        log.debug("Request to get category by name: {}", name);
        return categoryService.getCategoryByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        log.debug("Request to get all categories");
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Request to update category ID {}: {}", id, categoryDto.getName());
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable Long id) {
        log.info("Request to delete category ID: {}", id);
        categoryService.deleteCategory(id);
        // Assuming MessageResponse DTO exists or will be created in this service's 'payload.response' package
        return ResponseEntity.ok(new MessageResponse("Category deleted successfully!"));
    }
}
