package com.dreamcollections.services.cart.dto;

import java.math.BigDecimal;

// This DTO represents the data expected from Product Catalog Service for a variant
public class ProductVariantDetailDto {
    private Long id;
    private String size;
    private Integer stockQuantity;
    private Long productId; // ID of the parent product

    // Details from the parent Product, denormalized for convenience
    private String productName;
    private BigDecimal productPrice; // Assuming this is the price for the variant/product
    private String productImageUrl;

    // Constructors, Getters, Setters
    public ProductVariantDetailDto() {}

    public ProductVariantDetailDto(Long id, String size, Integer stockQuantity, Long productId,
                                   String productName, BigDecimal productPrice, String productImageUrl) {
        this.id = id;
        this.size = size;
        this.stockQuantity = stockQuantity;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
    }


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
