package com.dreamcollections.services.order.dto.response;

import com.dreamcollections.services.order.model.OrderStatus; // Enum from model
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class OrderResponseDto {
    private Long id; // Order ID
    private Long userId;
    private List<OrderItemResponseDto> items = new ArrayList<>();
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String shippingAddress;
    private String paymentTransactionId; // Can be null if payment not yet processed/recorded

    // Constructor
    public OrderResponseDto(Long id, Long userId, List<OrderItemResponseDto> items, BigDecimal totalAmount,
                            OrderStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                            String shippingAddress, String paymentTransactionId) {
        this.id = id;
        this.userId = userId;
        this.items = items != null ? items : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.shippingAddress = shippingAddress;
        this.paymentTransactionId = paymentTransactionId;
    }

    public OrderResponseDto() {} // Default constructor

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public List<OrderItemResponseDto> getItems() { return items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getShippingAddress() { return shippingAddress; }
    public String getPaymentTransactionId() { return paymentTransactionId; }

    // Setters might be useful if building the DTO step-by-step, or use a builder pattern
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setItems(List<OrderItemResponseDto> items) { this.items = items; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
}
