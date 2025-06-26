package com.dreamcollections.services.order.model;

public enum OrderStatus {
    PENDING_PAYMENT, // Order created, awaiting payment (initial state)
    AWAITING_PAYMENT_CONFIRMATION, // Payment initiated, waiting for confirmation from gateway
    PAID,            // Payment confirmed by gateway
    PROCESSING,      // Order is being prepared for shipment
    SHIPPED,         // Order has been shipped
    DELIVERED,       // Order has been delivered to customer
    CANCELLED,       // Order was cancelled (by user or system)
    REFUND_PENDING,  // Refund initiated
    REFUNDED,        // Order was refunded
    FAILED           // Order creation or payment failed critically
}
