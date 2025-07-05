package com.dreamcollections.services.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    @NotBlank(message = "Street address cannot be blank")
    @Size(max = 255, message = "Street address must be less than 255 characters")
    private String street;

    @Size(max = 100, message = "Address line 2 must be less than 100 characters")
    private String addressLine2; // Optional: Apt, Suite, Building, etc.

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @NotBlank(message = "State or Province cannot be blank")
    @Size(max = 100, message = "State or Province must be less than 100 characters")
    private String stateOrProvince;

    @NotBlank(message = "Postal code cannot be blank")
    @Size(max = 20, message = "Postal code must be less than 20 characters")
    private String postalCode;

    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100, message = "Country must be less than 100 characters")
    private String country;

    @NotBlank(message = "Contact phone number cannot be blank")
    @Size(min = 7, max = 20, message = "Contact phone number must be between 7 and 20 characters")
    // Add regex pattern validation for phone if specific formats are required, e.g., @Pattern(regexp = "^\\+?[0-9. ()-]{7,20}$")
    private String contactPhone;
}
