package com.dreamcollections.services.product.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String videoUrl;
    private CategoryDto category;
    private List<ProductVariantDto> variants;

    public ProductResponseDto(Long id, String name, String description, BigDecimal price,
                              String imageUrl, String videoUrl, CategoryDto category,
                              List<ProductVariantDto> variants) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.category = category;
        this.variants = variants;
    }

    // Lombok generates getters and setters
}
