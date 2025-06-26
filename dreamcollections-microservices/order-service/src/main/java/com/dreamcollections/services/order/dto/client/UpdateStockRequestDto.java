package com.dreamcollections.services.order.dto.client;

// This DTO is used to request a stock update in ProductCatalogService
public class UpdateStockRequestDto {
    private Integer stockQuantity; // The new stock quantity

    public UpdateStockRequestDto() {}

    public UpdateStockRequestDto(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
