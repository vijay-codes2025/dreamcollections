package com.dreamcollections.services.cart.dto;

import java.math.BigDecimal;

public class CartItemDto {
    private Long cartItemId; // ID of the CartItem entity itself
    private Long productVariantId;
    private String productName;
    private String productImageUrl;
    private String variantSize;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    public CartItemDto() {}

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

    // Getters and Setters
    public Long getCartItemId() { return cartItemId; }
    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
    public Long getProductVariantId() { return productVariantId; }
    public void setProductVariantId(Long productVariantId) { this.productVariantId = productVariantId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public String getVariantSize() { return variantSize; }
    public void setVariantSize(String variantSize) { this.variantSize = variantSize; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (this.unitPrice != null && this.quantity != null) {
            this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null && this.quantity != null) {
            this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
    public BigDecimal getSubtotal() { return subtotal; }
    // No setter for subtotal as it's calculated
}
