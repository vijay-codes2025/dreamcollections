package com.dreamcollections.services.order.client;

import com.dreamcollections.services.order.dto.client.CartDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity; // For void responses

// Name matches the application name of cart-service
// URL is for direct connection
@FeignClient(name = "cart-service", url = "${services.cart.url}")
public interface CartServiceClient {

    // Get cart for a user. CartController in cart-service uses "/carts/mine"
    // This means the Feign client needs to pass the Authorization header with the user's JWT.
    // Feign can be configured to propagate headers.
    // The path here should match the endpoint in CartController.
    // If CartController's getMyCart() derives userId from token, this call is fine.
    // If CartController expects /carts/{userId}, then this client needs to pass userId.
    // For now, assuming CartController's /carts/mine is called, and Feign will propagate Auth header.
    @GetMapping("/carts/mine") // Path in CartService's CartController
    CartDataDto getUserCart(); // This implies the call is made in the context of a user (auth header propagated)

    // Clear cart for a user after order creation.
    // Similar to above, assumes Authorization header is propagated.
    @DeleteMapping("/carts/mine") // Path in CartService's CartController
    ResponseEntity<Void> clearUserCart(); // Using ResponseEntity<Void> for empty response body
}
