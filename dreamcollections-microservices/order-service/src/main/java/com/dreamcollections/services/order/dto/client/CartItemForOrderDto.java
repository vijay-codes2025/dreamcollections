package com.dreamcollections.services.order.dto.client;

import java.math.BigDecimal;

// DTO representing a cart item as fetched from Cart Service
public class CartItemForOrderDto {
    // cartItemId might not be needed by order service, but productVariantId is crucial
    private Long productVariantId;
    // The following fields are useful for order creation, but might be re-fetched from Product Catalog
    // for canonical data at the moment of order creation. Cart service might provide them as current snapshot.
    private String productName;
    private String productImageUrl;
    private String variantSize;
    private BigDecimal unitPrice; // Price from cart (which should be current product price)
    private Integer quantity;
    private BigDecimal subtotal;

    // Getters and Setters
    public Long getProductVariantId() { return productVariantId; }
    public void setProductVariantId(Long productVariantId) { this.productVariantId = productVariantId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public String getVariantSize() { return variantSize; }
    public void setVariantSize(String variantSize) { this.variantSize = variantSize; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
