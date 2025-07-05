package com.dreamcollections.services.order.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "order_status_logs")
public class OrderStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status") // Can be null for the initial status log entry
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private OrderStatus newStatus;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by_user_id") // ID of the user (customer or admin) who triggered the change
    private String changedByUserId; // Using String to accommodate system users or admin user IDs from a different context

    @Column(length = 1000)
    private String notes; // Optional notes about the status change

    public OrderStatusLog(Order order, OrderStatus previousStatus, OrderStatus newStatus, String changedByUserId, String notes) {
        this.order = order;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedByUserId = changedByUserId;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
