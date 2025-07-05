package com.dreamcollections.services.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {

    @NotBlank(message = "Street address cannot be blank")
    @Size(max = 255)
    @Column(name = "shipping_street", length = 255) // Or billing_street depending on usage context
    private String street;

    @Size(max = 100)
    @Column(name = "shipping_address_line2", length = 100)
    private String addressLine2;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100)
    @Column(name = "shipping_city", length = 100)
    private String city;

    @NotBlank(message = "State or Province cannot be blank")
    @Size(max = 100)
    @Column(name = "shipping_state_province", length = 100)
    private String stateOrProvince;

    @NotBlank(message = "Postal code cannot be blank")
    @Size(max = 20)
    @Column(name = "shipping_postal_code", length = 20)
    private String postalCode;

    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100)
    @Column(name = "shipping_country", length = 100)
    private String country;

    @NotBlank(message = "Contact phone number cannot be blank")
    @Size(min = 7, max = 20, message = "Contact phone number must be between 7 and 20 characters")
    @Column(name = "shipping_contact_phone", length = 20, nullable = false) // Made nullable = false
    private String contactPhone;
}
