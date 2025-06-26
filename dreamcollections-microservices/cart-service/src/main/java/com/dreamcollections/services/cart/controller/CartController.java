package com.dreamcollections.services.cart.controller;

import com.dreamcollections.services.cart.dto.CartDto;
import com.dreamcollections.services.cart.payload.request.AddItemToCartRequestDto;
import com.dreamcollections.services.cart.payload.request.UpdateCartItemQuantityRequestDto;
import com.dreamcollections.services.cart.payload.response.MessageResponse; // Need this DTO
import com.dreamcollections.services.cart.service.CartService;
import com.dreamcollections.services.cart.exception.ForbiddenAccessException; // Custom exception for authz

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Spring's User object
import org.springframework.web.bind.annotation.*;

// For fetching User ID from Identity Service if not in JWT (more complex, not doing this now)
// We'll assume User ID is derivable from JWT subject (username) or a custom claim if needed.
// For now, let's assume the 'username' from JWT is the key, and CartService maps it to a userId if necessary,
// or that userId is directly a claim in JWT.
// The simplest for now: if JWT subject is a string that IS the userId.
// Or, we use username and have a utility/client to get ID from IdentityService (adds coupling).

// Let's assume for now the JWT `subject` IS the User ID (as a string).
// Or, more realistically, identity-service puts 'userId' as a claim.
// For CartController, we'll extract the username (subject) and then assume CartService uses this.
// The `CartServiceImpl` uses `Long userId`, so this controller needs to provide that.
// The `identity-service` AuthController puts User.id in the JwtResponse, but not as a standard claim in JWT.
// Let's modify identity-service's JwtUtils to add 'userId' as a claim.

// ---> This requires a change in identity-service's JwtUtils and AuthController.
// ---> For now, I will proceed assuming username is the principal and CartService will need to resolve it to userId.
// ---> OR, I can make CartController fetch User ID from Identity Service based on username.
// ---> Let's stick to: JWT principal's name is the username. CartService will use this username.
// ---> This means Cart model should store username, or CartService needs an IdentityServiceClient.
// ---> Plan stated Cart.userId. So JWT *must* contain userId.

// REVISITING: `identity-service`'s `JwtUtils.generateJwtToken` uses `userPrincipal.getUsername()` as subject.
// `JwtResponse` includes `user.getId()`.
// To make `userId` available to other services from JWT:
// 1. `identity-service`'s `JwtUtils` needs to add `userId` as a claim.
// 2. Resource services' `AuthTokenFilter` needs to extract `userId` claim.
// 3. Principal can be custom object holding username, userId, roles.

// For now, let's assume `AuthTokenFilter` in `cart-service` will be enhanced to extract `userId` claim.
// And `identity-service` `JwtUtils` will be updated to add it.
// I will make a note to update identity-service's JwtUtils later.
// For this controller, I'll assume `authentication.getPrincipal()` can give us a custom Principal with userId.
// Or, more simply, that UserDetails.getUsername() IS the userId (if Long.parseLong works).

// Simpler path for now: The UserDetails principal IS the username.
// The CartService will need to get the User object from IdentityService using username,
// or IdentityService needs to include userId in JWT.
// Let's assume IdentityService includes userId in JWT.

// I will create a helper in this controller to get userId from JWT claims.
// This means AuthTokenFilter should put userId into the Authentication details or principal.

// Correct approach:
// 1. identity-service JwtUtils: Add userId as a claim. (Will do this as a fix later)
// 2. cart-service AuthTokenFilter: Extract userId claim and make it available.
//    (e.g. by setting a custom principal object or adding to UserDetails properties if possible).
// For now, the `getAuthenticatedUserIdFromSecurityContext` will try to parse username as Long.
// This is a placeholder until JWT claim is properly set up.

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/carts") // Path within this service. API Gateway maps /api/carts
@PreAuthorize("isAuthenticated()") // All cart operations require an authenticated user
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    // Helper to get User ID from security context.
    // This assumes 'username' stored in UserDetails is actually the Long User ID.
    // This is a TEMPORARY simplification.
    // PROPER WAY: identity-service adds 'userId' claim to JWT.
    // cart-service's AuthTokenFilter extracts it and makes it available via Authentication object.
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            throw new ForbiddenAccessException("User not authenticated.");
        }
        // Temporary: Assuming the username IS the userId as a string.
        // In a real scenario, AuthTokenFilter would parse a 'userId' claim from JWT
        // and potentially set a custom Principal object or make it available.
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // This is a placeholder. The username from UserDetails is typically a string username.
            // We need a 'userId' claim in the JWT.
            // For now, we'll rely on the service layer to handle userId passed in path,
            // and use @PreAuthorize to check ownership if userId is in path.
            // This controller will just pass what it gets.
            // The path variable {userId} will be used by the service.
            // Security will be: @PreAuthorize("#userId == authentication.principal.id") or similar.
            // This means our UserDetails principal in AuthTokenFilter needs to be enriched or be a custom object.

            // For now, let's assume the path variable {userIdFromPath} must match the authenticated principal's ID.
            // This controller won't extract it itself but rely on @PreAuthorize.
            // The methods will take {userIdFromPath} and PreAuthorize will check it.
            // This requires modifying AuthTokenFilter to put a principal that has an 'id' field.
            // Spring's User object (used as principal in AuthTokenFilter) doesn't have an 'id' field.
            // Let's make AuthTokenFilter use a custom principal or pass userId via Authentication.getDetails()
            // For now, I'll make the controller methods take userId from path and assume service layer / PreAuthorize handles it.
            // The current AuthTokenFilter uses Spring's User(username, "", authorities).
            // A simple way: parse username to Long if it's expected to be the ID.
            return Long.parseLong( ((UserDetails) authentication.getPrincipal()).getUsername() );

        } catch (NumberFormatException e) {
            log.error("Could not parse userId from username in JWT principal: {}", authentication.getPrincipal());
            throw new ForbiddenAccessException("User ID not found or invalid in token.");
        } catch (ClassCastException e) {
            log.error("Principal is not of type UserDetails: {}", authentication.getPrincipal());
            throw new ForbiddenAccessException("Invalid authentication principal type.");
        }
    }


    // GET /carts - Gets the cart for the authenticated user.
    // The userId is derived from the token.
    @GetMapping("/mine")
    public ResponseEntity<CartDto> getMyCart() {
        Long userId = getAuthenticatedUserId();
        log.info("Request to get cart for authenticated user ID (derived): {}", userId);
        CartDto cartDto = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartDto);
    }

    // POST /carts/mine/items - Adds item to the authenticated user's cart.
    @PostMapping("/mine/items")
    public ResponseEntity<CartDto> addItemToMyCart(@Valid @RequestBody AddItemToCartRequestDto addItemDto) {
        Long userId = getAuthenticatedUserId();
        log.info("Request to add item to cart for user ID (derived) {}: VariantID {}, Quantity {}", userId, addItemDto.getProductVariantId(), addItemDto.getQuantity());
        CartDto cartDto = cartService.addItemToCart(userId, addItemDto);
        return ResponseEntity.ok(cartDto);
    }

    // PUT /carts/mine/items/{productVariantId} - Updates item in authenticated user's cart.
    @PutMapping("/mine/items/{productVariantId}")
    public ResponseEntity<CartDto> updateMyCartItemQuantity(
            @PathVariable Long productVariantId,
            @Valid @RequestBody UpdateCartItemQuantityRequestDto updateQuantityDto) {
        Long userId = getAuthenticatedUserId();
        log.info("Request to update cart item for user ID (derived) {}: VariantID {}, New Quantity {}", userId, productVariantId, updateQuantityDto.getQuantity());
        CartDto cartDto = cartService.updateCartItemQuantity(userId, productVariantId, updateQuantityDto);
        return ResponseEntity.ok(cartDto);
    }

    // DELETE /carts/mine/items/{productVariantId} - Removes item from authenticated user's cart.
    @DeleteMapping("/mine/items/{productVariantId}")
    public ResponseEntity<CartDto> removeMyItemFromCart(@PathVariable Long productVariantId) {
        Long userId = getAuthenticatedUserId();
        log.info("Request to remove item VariantID {} from cart for user ID (derived) {}", productVariantId, userId);
        CartDto cartDto = cartService.removeItemFromCart(userId, productVariantId);
        return ResponseEntity.ok(cartDto);
    }

    // DELETE /carts/mine - Clears the authenticated user's cart.
    @DeleteMapping("/mine")
    public ResponseEntity<MessageResponse> clearMyCart() {
        Long userId = getAuthenticatedUserId();
        log.info("Request to clear cart for user ID (derived) {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.ok(new MessageResponse("Cart cleared successfully!"));
    }

    // --- Admin endpoint example (if needed, not part of current user flow) ---
    // GET /carts/user/{actualUserId} - Admin gets a specific user's cart.
    @GetMapping("/user/{actualUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CartDto> getUserCart(@PathVariable Long actualUserId) {
        log.info("ADMIN request to get cart for user ID: {}", actualUserId);
        CartDto cartDto = cartService.getCartByUserId(actualUserId);
        return ResponseEntity.ok(cartDto);
    }
}
