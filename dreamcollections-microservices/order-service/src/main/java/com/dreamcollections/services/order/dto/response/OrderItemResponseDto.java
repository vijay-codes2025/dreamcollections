package com.dreamcollections.services.order.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
// AllArgsConstructor might be too verbose if we want to keep the subtotal logic in constructor
// For DTOs primarily for response, getters are key. Lombok @Data provides them.

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderItemResponseDto {
    private Long id; // OrderItem ID
    private Long productVariantId;
    private String productName;
    private String variantSize; // e.g., "M", "Ring Size 7", "18 inch chain"
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal priceAtPurchase; // Price per unit at the time of order
    private BigDecimal subtotal; // quantity * priceAtPurchase

    // Custom constructor to handle subtotal calculation and potentially other logic
    public OrderItemResponseDto(Long id, Long productVariantId, String productName, String variantSize,
                                String productImageUrl, Integer quantity, BigDecimal priceAtPurchase) {
        this.id = id;
        this.productVariantId = productVariantId;
        this.productName = productName;
        this.variantSize = variantSize;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        if (this.quantity != null && this.priceAtPurchase != null) {
            this.subtotal = this.priceAtPurchase.multiply(BigDecimal.valueOf(this.quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }
    // Lombok's @Data will generate getters for all fields, and setters (which are fine for DTOs).
    // It will also generate equals, hashCode, and toString.
}
