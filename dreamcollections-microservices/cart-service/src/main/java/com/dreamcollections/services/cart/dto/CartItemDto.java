package com.dreamcollections.services.cart.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartItemDto {
    private Long cartItemId; // ID of the CartItem entity itself
    private Long productVariantId;
    private String productName;
    private String productImageUrl;
    private String variantSize;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    public CartItemDto(Long cartItemId, Long productVariantId, String productName, String productImageUrl,
                       String variantSize, BigDecimal unitPrice, Integer quantity) {
        this.cartItemId = cartItemId;
        this.productVariantId = productVariantId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.variantSize = variantSize;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    // Custom setters to keep subtotal calculation logic
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (this.unitPrice != null && this.quantity != null) {
            this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null && this.quantity != null) {
            this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
    // Getter for subtotal is provided by Lombok
}
