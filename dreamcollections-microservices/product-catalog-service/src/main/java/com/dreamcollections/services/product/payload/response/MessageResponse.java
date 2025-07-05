package com.dreamcollections.services.product.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message;
    private boolean success = true; // Default to true for convenience like new MessageResponse("Done")

    public MessageResponse(String message) {
        this.message = message;
        this.success = true; // Explicitly set for the single-argument constructor
    }
    // For the (String, boolean) case, @AllArgsConstructor will cover it.
    // If only (String) and (String, boolean) constructors are needed,
    // @NoArgsConstructor might be less useful unless default construction is expected.
    // Let's keep @NoArgsConstructor and @AllArgsConstructor for general utility,
    // and retain the specific constructor for the common case.
}
