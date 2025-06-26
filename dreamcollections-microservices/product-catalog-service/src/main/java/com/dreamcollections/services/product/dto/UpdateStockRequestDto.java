package com.dreamcollections.services.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateStockRequestDto {

    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
