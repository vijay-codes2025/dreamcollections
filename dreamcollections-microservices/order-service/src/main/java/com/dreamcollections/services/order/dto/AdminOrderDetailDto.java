package com.dreamcollections.services.order.dto;

import com.dreamcollections.services.order.dto.response.OrderItemResponseDto; // Correct import
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderDetailDto {
    private Long id;
    private Long userId;
    private String customerEmail; // Email used for the order
    private String customerNameSnapshot; // Name of customer at the time of order
    private LocalDateTime orderDate;
    private LocalDateTime lastUpdated; // When the order was last modified
    private BigDecimal totalAmount;
    private String status; // Current status of the order
    private List<OrderItemResponseDto> items; // Use the existing OrderItemResponseDto
    private AddressDto shippingAddress; // Use the new AddressDto
    private AddressDto billingAddress; // Optional, if different from shipping
    private String paymentMethod; // e.g., "Credit Card", "PayPal" (could be more structured)
    private String paymentStatus; // e.g., "PAID", "PENDING", "FAILED"
    private String shippingMethod; // e.g., "Standard Shipping", "Express"
    private String trackingNumber; // If shipped

    // For future enhancements:
    // private List<OrderStatusLogDto> statusHistory;
    // private List<AdminOrderNoteDto> adminNotes;
}
