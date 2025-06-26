package com.dreamcollections.services.order.dto.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

// DTO representing the entire cart as fetched from Cart Service
public class CartDataDto {
    private Long cartId;
    private Long userId;
    private List<CartItemForOrderDto> items = new ArrayList<>();
    private BigDecimal totalPrice; // Total price from cart, for cross-checking or display
    private int totalItemsCount;

    // Getters and Setters
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<CartItemForOrderDto> getItems() { return items; }
    public void setItems(List<CartItemForOrderDto> items) { this.items = items; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public int getTotalItemsCount() { return totalItemsCount; }
    public void setTotalItemsCount(int totalItemsCount) { this.totalItemsCount = totalItemsCount; }
}
