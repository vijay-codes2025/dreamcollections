package com.dreamcollections.services.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductVariantDto {
    private Long id;

    @NotBlank(message = "Variant size cannot be blank")
    @Size(max = 50, message = "Size description must be less than 50 characters")
    private String size;

    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private Long productId; // Included for context in responses

    public ProductVariantDto(Long id, String size, Integer stockQuantity, Long productId) {
        this.id = id;
        this.size = size;
        this.stockQuantity = stockQuantity;
        this.productId = productId;
    }

    // Constructor without productId, useful for requests where productId is implicit
    public ProductVariantDto(Long id, String size, Integer stockQuantity) {
        this.id = id;
        this.size = size;
        this.stockQuantity = stockQuantity;
    }


    // Lombok generates getters and setters
}
