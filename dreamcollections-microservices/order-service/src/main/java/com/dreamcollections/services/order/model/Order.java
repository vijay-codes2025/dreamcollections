package com.dreamcollections.services.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders") // "order" is often a reserved keyword in SQL
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // From Identity Service

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "shipping_street")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "shipping_address_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
            @AttributeOverride(name = "stateOrProvince", column = @Column(name = "shipping_state_province")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "shipping_country")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "shipping_contact_phone"))
    })
    private Address shippingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "billing_address_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "stateOrProvince", column = @Column(name = "billing_state_province")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "billing_country")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "billing_contact_phone"))
    })
    private Address billingAddress; // Optional, can be null if same as shipping

    // Example: Store payment intent ID or transaction ID from payment gateway
    @Column(name = "payment_transaction_id")
    private String paymentTransactionId;

    // New fields for more descriptive orders
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_name_snapshot", nullable = false)
    private String customerNameSnapshot; // Customer name at the time of order

    @Column(name = "payment_method") // e.g., "Credit Card", "PayPal"
    private String paymentMethod;

    @Enumerated(EnumType.STRING) // Or use a String if statuses are very dynamic from PSP
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus; // e.g., PENDING, PAID, FAILED

    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "tracking_number")
    private String trackingNumber;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<OrderItem> items = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("changedAt DESC") // Show newest status logs first
    private Set<OrderStatusLog> statusLogs = new HashSet<>();

    // Constructors
    public Order(Long userId, String customerEmail, String customerNameSnapshot,
                 BigDecimal totalAmount, OrderStatus status, PaymentStatus paymentStatus,
                 Address shippingAddress, Address billingAddress, String paymentMethod) {
        this.userId = userId;
        this.customerEmail = customerEmail;
        this.customerNameSnapshot = customerNameSnapshot;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod;
    }

    // Lombok generates getters and setters

    // Helper methods
    public void addStatusLog(OrderStatusLog statusLog) {
        statusLogs.add(statusLog);
        statusLog.setOrder(this);
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (this.status == null) { // Default status if not set
            this.status = OrderStatus.PENDING_PAYMENT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
