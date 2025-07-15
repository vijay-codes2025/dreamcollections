package com.dreamcollections.services.identity.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OtpRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format (e.g., +919876543210)")
    private String phoneNumber;

    @Email(message = "Email should be valid")
    private String email; // Optional

    private String type = "LOGIN"; // LOGIN, CHECKOUT, REGISTRATION
}
