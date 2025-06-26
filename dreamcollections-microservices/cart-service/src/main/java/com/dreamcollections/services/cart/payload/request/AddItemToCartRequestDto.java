package com.dreamcollections.services.cart.payload.request; // Changed to payload.request

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddItemToCartRequestDto {

    @NotNull(message = "Product Variant ID cannot be null")
    private Long productVariantId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public Long getProductVariantId() { return productVariantId; }
    public void setProductVariantId(Long productVariantId) { this.productVariantId = productVariantId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
