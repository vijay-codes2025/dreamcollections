package com.dreamcollections.services.order.dto.response; // Changed to dto.response

import java.math.BigDecimal;

public class OrderItemResponseDto {
    private Long id; // OrderItem ID
    private Long productVariantId;
    private String productName;
    private String variantSize;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal priceAtPurchase; // Price per unit
    private BigDecimal subtotal; // quantity * priceAtPurchase

    public OrderItemResponseDto(Long id, Long productVariantId, String productName, String variantSize,
                                String productImageUrl, Integer quantity, BigDecimal priceAtPurchase) {
        this.id = id;
        this.productVariantId = productVariantId;
        this.productName = productName;
        this.variantSize = variantSize;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        if (quantity != null && priceAtPurchase != null) {
            this.subtotal = priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    // Getters (setters not typically needed for response DTOs)
    public Long getId() { return id; }
    public Long getProductVariantId() { return productVariantId; }
    public String getProductName() { return productName; }
    public String getVariantSize() { return variantSize; }
    public String getProductImageUrl() { return productImageUrl; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public BigDecimal getSubtotal() { return subtotal; }
}
