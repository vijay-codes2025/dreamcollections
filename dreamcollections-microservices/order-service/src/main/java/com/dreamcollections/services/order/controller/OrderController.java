package com.dreamcollections.services.order.controller;

import com.dreamcollections.services.order.dto.request.CreateOrderRequestDto;
import com.dreamcollections.services.order.dto.response.OrderResponseDto;
import com.dreamcollections.services.order.security.UserPrincipal; // Custom principal
import com.dreamcollections.services.order.service.OrderService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // To inject UserPrincipal
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600) // API Gateway should handle external CORS
@RestController
@RequestMapping("/orders") // Path within this service. API Gateway maps /api/orders
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping
    @PreAuthorize("isAuthenticated()") // Any authenticated user can create an order for themselves
    public ResponseEntity<OrderResponseDto> createOrder(
            @AuthenticationPrincipal UserPrincipal principal, // Injects our custom principal
            @Valid @RequestBody CreateOrderRequestDto createOrderRequestDto) {

        Long userId = principal.getId(); // Get userId from our custom principal
        log.info("Request from user ID {} to create order.", userId);
        OrderResponseDto createdOrder = orderService.createOrder(userId, createOrderRequestDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    // User can only get their own order. Admin could have a separate endpoint or broader access.
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable Long orderId,
            // @RequestParam Long userId, // Or get userId from path like /user/{userId}/order/{orderId}
            @AuthenticationPrincipal UserPrincipal principal) { // principal.id is the authenticated user's ID

        // The @PreAuthorize above handles the security check.
        // If we didn't use #userId in PreAuthorize, we'd fetch principal.getId() here for the service call.
        // For this example, let's assume the service method needs the userId explicitly for the query.
        Long authenticatedUserId = principal.getId();
        log.info("Request from user ID {} to get order ID {}.", authenticatedUserId, orderId);

        return orderService.getOrderByIdAndUserId(orderId, authenticatedUserId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my-orders") // A dedicated endpoint for user's own orders
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {

        Long userId = principal.getId();
        log.info("Request from user ID {} to get their orders. Page: {}, Size: {}", userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<OrderResponseDto> orders = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    // Example Admin endpoint (if needed)
    // @GetMapping("/admin/user/{userId}")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<Page<OrderResponseDto>> getUserOrdersForAdmin(
    //         @PathVariable Long userId,
    //         @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {
    //     log.info("ADMIN request to get orders for user ID {}.", userId);
    //     Page<OrderResponseDto> orders = orderService.getOrdersByUserId(userId, pageable); // Same service method
    //     return ResponseEntity.ok(orders);
    // }
}
