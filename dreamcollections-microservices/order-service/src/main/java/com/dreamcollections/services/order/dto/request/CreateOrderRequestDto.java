package com.dreamcollections.services.order.dto.request; // Changed to dto.request

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Other fields like paymentMethodId could be added later

public class CreateOrderRequestDto {

    // UserId will be taken from JWT/path, not from request body for security.
    // CartId is also usually derived by the service based on UserId.

    @NotBlank(message = "Shipping address cannot be blank")
    @Size(min = 10, max = 500, message = "Shipping address must be between 10 and 500 characters")
    private String shippingAddress;

    // Optional: A idempotency key to prevent duplicate order creation on retries
    // private String idempotencyKey;

    // Getters and Setters
    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    // public String getIdempotencyKey() { return idempotencyKey; }
    // public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
