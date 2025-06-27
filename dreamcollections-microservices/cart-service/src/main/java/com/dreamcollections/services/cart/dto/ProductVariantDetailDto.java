package com.dreamcollections.services.cart.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// This DTO represents the data expected from Product Catalog Service for a variant
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDetailDto {
    private Long id;
    private String size;
    private Integer stockQuantity;
    private Long productId; // ID of the parent product

    // Details from the parent Product, denormalized for convenience
    private String productName;
    private BigDecimal productPrice; // Assuming this is the price for the variant/product
    private String productImageUrl;

    // Lombok generates constructors, getters and setters
}
