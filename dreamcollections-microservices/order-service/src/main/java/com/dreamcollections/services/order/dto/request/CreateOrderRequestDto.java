package com.dreamcollections.services.order.dto.request;

import com.dreamcollections.services.order.dto.AddressDto; // Import AddressDto
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

// Other fields like paymentMethodId could be added later

@Data
@NoArgsConstructor
public class CreateOrderRequestDto {

    // UserId will be taken from JWT/path, not from request body for security.
    // CartId is also usually derived by the service based on UserId.

    @NotNull(message = "Shipping address cannot be null")
    @Valid // Enable validation of AddressDto fields
    private AddressDto shippingAddress;

    @Valid // Enable validation of AddressDto fields if billingAddress is provided
    private AddressDto billingAddress; // Optional, if not provided, can assume same as shipping or handle accordingly

    @NotBlank(message = "Customer email cannot be blank")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    @Size(max = 255)
    private String customerEmail;

    @NotBlank(message = "Customer name cannot be blank")
    @Size(max = 255)
    private String customerNameSnapshot; // Name as provided at checkout

    @NotBlank(message = "Payment method cannot be blank")
    @Size(max = 100)
    private String paymentMethod; // e.g., "CREDIT_CARD", "PAYPAL" - could be an enum DTO later

    @Size(max = 100)
    private String shippingMethod; // Optional at creation, e.g. "STANDARD", "EXPRESS"


    // Optional: A idempotency key to prevent duplicate order creation on retries
    // private String idempotencyKey;

    // Lombok's @Data will generate getters and setters
}
