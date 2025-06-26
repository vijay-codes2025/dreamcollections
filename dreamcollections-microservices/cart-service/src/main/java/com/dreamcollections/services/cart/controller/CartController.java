package com.dreamcollections.services.cart.controller;

import com.dreamcollections.services.cart.dto.CartDto;
import com.dreamcollections.services.cart.payload.request.AddItemToCartRequestDto;
import com.dreamcollections.services.cart.payload.request.UpdateCartItemQuantityRequestDto;
import com.dreamcollections.services.cart.payload.response.MessageResponse;
import com.dreamcollections.services.cart.service.CartService;
import com.dreamcollections.services.cart.exception.ForbiddenAccessException;
import com.dreamcollections.services.cart.security.UserPrincipal; // Import custom UserPrincipal

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/carts")
@PreAuthorize("isAuthenticated()")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    private void validatePrincipal(UserPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new ForbiddenAccessException("User ID not available in token. Ensure JWT contains 'userId' claim and AuthTokenFilter populates UserPrincipal correctly.");
        }
    }

    @GetMapping("/mine")
    public ResponseEntity<CartDto> getMyCart(@AuthenticationPrincipal UserPrincipal principal) {
        validatePrincipal(principal);
        Long userId = principal.getId();
        log.info("Request to get cart for authenticated user ID: {}", userId);
        CartDto cartDto = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/mine/items")
    public ResponseEntity<CartDto> addItemToMyCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddItemToCartRequestDto addItemDto) {
        validatePrincipal(principal);
        Long userId = principal.getId();
        log.info("Request to add item to cart for user ID {}: VariantID {}, Quantity {}", userId, addItemDto.getProductVariantId(), addItemDto.getQuantity());
        CartDto cartDto = cartService.addItemToCart(userId, addItemDto);
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/mine/items/{productVariantId}")
    public ResponseEntity<CartDto> updateMyCartItemQuantity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productVariantId,
            @Valid @RequestBody UpdateCartItemQuantityRequestDto updateQuantityDto) {
        validatePrincipal(principal);
        Long userId = principal.getId();
        log.info("Request to update cart item for user ID {}: VariantID {}, New Quantity {}", userId, productVariantId, updateQuantityDto.getQuantity());
        CartDto cartDto = cartService.updateCartItemQuantity(userId, productVariantId, updateQuantityDto);
        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping("/mine/items/{productVariantId}")
    public ResponseEntity<CartDto> removeMyItemFromCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productVariantId) {
        validatePrincipal(principal);
        Long userId = principal.getId();
        log.info("Request to remove item VariantID {} from cart for user ID {}", productVariantId, userId);
        CartDto cartDto = cartService.removeItemFromCart(userId, productVariantId);
        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping("/mine")
    public ResponseEntity<MessageResponse> clearMyCart(@AuthenticationPrincipal UserPrincipal principal) {
        validatePrincipal(principal);
        Long userId = principal.getId();
        log.info("Request to clear cart for user ID {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.ok(new MessageResponse("Cart cleared successfully!"));
    }

    // Admin endpoint to get a specific user's cart.
    @GetMapping("/user/{userIdFromPath}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can access this
    public ResponseEntity<CartDto> getUserCartByAdmin(
            @PathVariable Long userIdFromPath,
            @AuthenticationPrincipal UserPrincipal adminPrincipal) { // Ensure admin is authenticated
        log.info("ADMIN ({}) request to get cart for user ID: {}", adminPrincipal.getUsername(), userIdFromPath);
        CartDto cartDto = cartService.getCartByUserId(userIdFromPath);
        return ResponseEntity.ok(cartDto);
    }
}
