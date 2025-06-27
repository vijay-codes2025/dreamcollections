package com.dreamcollections.services.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId; // From Product Catalog Service

    // Denormalized product information at the time of order for historical accuracy
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "variant_size") // Nullable if not applicable
    private String variantSize;

    @Column(name = "product_image_url")
    private String productImageUrl;


    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase; // Price of a single unit at the time of purchase

    // Constructors
    public OrderItem(Order order, Long productVariantId, String productName, String variantSize, String productImageUrl, Integer quantity, BigDecimal priceAtPurchase) {
        this.order = order;
        this.productVariantId = productVariantId;
        this.productName = productName;
        this.variantSize = variantSize;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }
    // Lombok generates getters, setters, equals, hashCode, toString
}
