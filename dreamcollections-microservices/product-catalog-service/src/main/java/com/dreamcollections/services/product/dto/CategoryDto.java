package com.dreamcollections.services.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description can be up to 500 characters")
    private String description;

    private Long parentId; // ID of the parent category

    public CategoryDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public CategoryDto(Long id, String name, String description, Long parentId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentId = parentId;
    }
    // Lombok generates getters and setters
}
