package com.dreamcollections.services.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long parentId; // ID of the parent category
    private String parentName; // Name of the parent category
    private List<CategoryResponseDto> subCategories; // List of DTOs for sub-categories

    public CategoryResponseDto(Long id, String name, String description, Long parentId, String parentName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.parentName = parentName;
    }
}
