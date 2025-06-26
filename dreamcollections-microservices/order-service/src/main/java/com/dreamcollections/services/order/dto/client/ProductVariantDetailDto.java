package com.dreamcollections.services.order.dto.client; // Suffix 'client' for clarity

import java.math.BigDecimal;

// DTO representing product variant details fetched from Product Catalog Service
public class ProductVariantDetailDto {
    private Long id;
    private String size;
    private Integer stockQuantity;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private String productImageUrl;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
}
