package com.dreamcollections.services.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderSummaryDto {
    private Long id;
    private Long userId; // Assuming we primarily work with userId from token/identity
    private String customerEmail; // Or some direct customer identifier stored with the order
    private String customerNameSnapshot; // Name of customer at the time of order
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status; // e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    private int itemCount;
}
