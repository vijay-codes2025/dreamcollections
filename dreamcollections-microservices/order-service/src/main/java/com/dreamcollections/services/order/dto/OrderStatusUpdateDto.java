package com.dreamcollections.services.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDto {

    @NotBlank(message = "New status cannot be blank")
    @Size(max = 50, message = "Status must be less than 50 characters")
    // We might want to add a custom validator later to ensure this status
    // matches one of the predefined OrderStatus enum values.
    private String newStatus;

    // Optional: A field for admin notes related to this status change
    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;
}
