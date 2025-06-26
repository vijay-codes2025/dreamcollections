package com.dreamcollections.services.cart.service;

import com.dreamcollections.services.cart.dto.CartDto;
import com.dreamcollections.services.cart.payload.request.AddItemToCartRequestDto;
import com.dreamcollections.services.cart.payload.request.UpdateCartItemQuantityRequestDto;

public interface CartService {
    CartDto getCartByUserId(Long userId);
    CartDto addItemToCart(Long userId, AddItemToCartRequestDto addItemDto);
    CartDto updateCartItemQuantity(Long userId, Long productVariantId, UpdateCartItemQuantityRequestDto updateQuantityDto);
    CartDto removeItemFromCart(Long userId, Long productVariantId);
    void clearCart(Long userId);
    // Internal method used by OrderService perhaps
    CartDto getCartForCheckout(Long userId); // Could be same as getCartByUserId or more specific
}
