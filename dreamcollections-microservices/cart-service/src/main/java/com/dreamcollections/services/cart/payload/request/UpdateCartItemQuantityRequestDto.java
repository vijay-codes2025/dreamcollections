package com.dreamcollections.services.cart.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateCartItemQuantityRequestDto {

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be 0 or greater. To remove an item, quantity should be 0 or use the delete item endpoint.")
    private Integer quantity;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
