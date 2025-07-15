package com.dreamcollections.services.identity.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Login ID (phone number or email) cannot be blank")
    private String loginId; // Can be phone number or email

    @NotBlank(message = "Password cannot be blank")
    private String password;

    // Legacy support for username field
    private String username;

    // Lombok will generate getters and setters
}
