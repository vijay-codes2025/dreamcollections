package com.dreamcollections.services.cart.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cart_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cart_id", "product_variant_id"}) // Ensure a variant appears only once per cart
})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId; // Store Product Variant ID from Product Catalog Service

    @Column(nullable = false)
    private Integer quantity;

    // Price at the time of adding to cart could be stored here if needed,
    // but typically cart reflects current prices. OrderItem will store priceAtPurchase.
    // For now, CartItem DTO will fetch current price from Product Catalog service.

    // Constructors
    public CartItem() {
    }

    public CartItem(Cart cart, Long productVariantId, Integer quantity) {
        this.cart = cart;
        this.productVariantId = productVariantId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id) &&
               Objects.equals(cart != null ? cart.getId() : null, cartItem.cart != null ? cartItem.cart.getId() : null) &&
               Objects.equals(productVariantId, cartItem.productVariantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cart != null ? cart.getId() : null, productVariantId);
    }

    @Override
    public String toString() {
        return "CartItem{" +
               "id=" + id +
               ", cartId=" + (cart != null ? cart.getId() : "null") +
               ", productVariantId=" + productVariantId +
               ", quantity=" + quantity +
               '}';
    }
}
