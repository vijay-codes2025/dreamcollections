package com.dreamcollections.services.cart.payload.request; // Changed to payload.request

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddItemToCartRequestDto {

    @NotNull(message = "Product Variant ID cannot be null")
    private Long productVariantId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // Lombok generates getters and setters
}
