package com.dreamcollections.services.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

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
    public OrderItem() {
    }

    public OrderItem(Order order, Long productVariantId, String productName, String variantSize, String productImageUrl, Integer quantity, BigDecimal priceAtPurchase) {
        this.order = order;
        this.productVariantId = productVariantId;
        this.productName = productName;
        this.variantSize = variantSize;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Long getProductVariantId() { return productVariantId; }
    public void setProductVariantId(Long productVariantId) { this.productVariantId = productVariantId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getVariantSize() { return variantSize; }
    public void setVariantSize(String variantSize) { this.variantSize = variantSize; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id) &&
               Objects.equals(order != null ? order.getId() : null, orderItem.order != null ? orderItem.order.getId() : null) &&
               Objects.equals(productVariantId, orderItem.productVariantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order != null ? order.getId() : null, productVariantId);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
               "id=" + id +
               ", orderId=" + (order != null ? order.getId() : "null") +
               ", productVariantId=" + productVariantId +
               ", productName='" + productName + '\'' +
               ", quantity=" + quantity +
               ", priceAtPurchase=" + priceAtPurchase +
               '}';
    }
}
