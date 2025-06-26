package com.dreamcollections.services.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList; // Initialize lists

public class CartDto {
    private Long cartId;
    private Long userId;
    private List<CartItemDto> items = new ArrayList<>(); // Initialize to avoid nulls
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private int totalItemsCount = 0; // Sum of quantities of all cart items

    public CartDto() {}

    public CartDto(Long cartId, Long userId, List<CartItemDto> items) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = (items == null) ? new ArrayList<>() : items; // Ensure list is not null
        recalculateTotals();
    }

    public void recalculateTotals() {
        if (this.items == null || this.items.isEmpty()) {
            this.totalPrice = BigDecimal.ZERO;
            this.totalItemsCount = 0;
        } else {
            this.totalPrice = this.items.stream()
                                   .map(CartItemDto::getSubtotal)
                                   .filter(java.util.Objects::nonNull) // Ensure subtotal is not null
                                   .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.totalItemsCount = this.items.stream()
                                   .filter(item -> item.getQuantity() != null) // Ensure quantity is not null
                                   .mapToInt(CartItemDto::getQuantity)
                                   .sum();
        }
    }


    // Getters and Setters
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) {
        this.items = (items == null) ? new ArrayList<>() : items;
        recalculateTotals(); // Recalculate when items are set
    }
    public BigDecimal getTotalPrice() { return totalPrice; }
    // No direct setter for totalPrice
    public int getTotalItemsCount() { return totalItemsCount; }
    // No direct setter for totalItemsCount
}
