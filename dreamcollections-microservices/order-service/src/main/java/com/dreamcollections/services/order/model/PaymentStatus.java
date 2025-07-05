package com.dreamcollections.services.order.model;

public enum PaymentStatus {
    PENDING,        // Payment initiated but not yet confirmed or failed
    PAID,           // Payment successfully completed
    FAILED,         // Payment attempt failed
    REFUND_PENDING, // Refund process initiated
    REFUNDED,       // Payment has been refunded
    PARTIALLY_REFUNDED // Only part of the payment has been refunded
}
