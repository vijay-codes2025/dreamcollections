package com.dreamcollections.services.order.controller;

import com.dreamcollections.services.order.dto.AdminOrderDetailDto;
import com.dreamcollections.services.order.dto.AdminOrderSummaryDto;
import com.dreamcollections.services.order.dto.OrderStatusUpdateDto;
import com.dreamcollections.services.order.model.Order;
import com.dreamcollections.services.order.model.OrderStatus;
import com.dreamcollections.services.order.service.OrderService;
// import com.sipios.springsearch.anotation.SearchSpec; // Removed dependency
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')") // Secure all endpoints in this controller for ADMIN role
public class AdminOrderController {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);

    private final OrderService orderService;

    @Autowired
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Retrieves a paginated list of orders for administrators.
     * Supports filtering via query parameters using Spring Data JPA Specifications.
     * Example filters: ?status=PROCESSING&userId=123&orderDate>=2023-01-01
     * The @SearchSpec annotation from an external library like 'spring-search' can simplify this,
     * or we can build specifications manually based on request parameters.
     * For now, we'll use a simple status filter and demonstrate how more complex specs could be added.
     */
    @GetMapping
    public ResponseEntity<Page<AdminOrderSummaryDto>> getAllOrdersForAdmin(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId
            // More params like date ranges can be added here to build a more complex Specification
            // For a more advanced solution, consider using a library like 'spring-search' for @SearchSpec
    ) {
        log.info("Admin request: Get all orders. Page: {}, Size: {}, Sort: {}, Status Filter: {}, UserId Filter: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort(), status, userId);

        // Build Specification dynamically based on request parameters
        Specification<Order> spec = Specification.where(null); // Start with a no-op spec

        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("status"), orderStatus));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter value provided: {}", status);
                // Optionally, throw BadRequestException or ignore invalid filter
            }
        }

        if (userId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("userId"), userId));
        }

        // Example for date range (if you add date request params)
        // if (startDate != null) {
        //     spec = spec.and((root, query, criteriaBuilder) ->
        //             criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay()));
        // }
        // if (endDate != null) {
        //     spec = spec.and((root, query, criteriaBuilder) ->
        //             criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate.atTime(23, 59, 59)));
        // }


        Page<AdminOrderSummaryDto> orders = orderService.getAllOrdersForAdmin(spec, pageable);
        return ResponseEntity.ok(orders);
    }

    // Endpoints for GET /{orderId}, PUT /{orderId}/status, POST /{orderId}/notes will be added next.

    @GetMapping("/{orderId}")
    public ResponseEntity<AdminOrderDetailDto> getOrderDetailsForAdmin(@PathVariable Long orderId) {
        log.info("Admin request: Get order details for ID {}.", orderId);
        Optional<AdminOrderDetailDto> orderDetails = orderService.getOrderDetailsForAdmin(orderId);
        return orderDetails
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<AdminOrderDetailDto> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateDto statusUpdateDto,
            @RequestHeader(name = "X-Admin-Username", required = false, defaultValue = "UnknownAdmin") String adminUsername // Example: Get admin user from custom header or security context
            // Alternatively, get admin username from SecurityContextHolder.getContext().getAuthentication().getName()
    ) {
        log.info("Admin request: Update status for order ID {} to {} by admin {}.", orderId, statusUpdateDto.getNewStatus(), adminUsername);
        // String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName(); // Better way if Spring Security context is populated

        AdminOrderDetailDto updatedOrder = orderService.updateOrderStatus(orderId, statusUpdateDto.getNewStatus(), adminUsername);
        // Consider also adding the notes from statusUpdateDto.getNotes() to the OrderStatusLog if that's desired
        if (statusUpdateDto.getNotes() != null && !statusUpdateDto.getNotes().isEmpty()) {
            // This would ideally be part of the service layer's updateOrderStatus or a separate call if notes are distinct
            // For now, the service's updateOrderStatus adds a generic note. This could be an enhancement.
            log.info("Admin note provided with status update for order {}: {}", orderId, statusUpdateDto.getNotes());
        }
        return ResponseEntity.ok(updatedOrder);
    }

    // For simplicity, let's assume OrderNoteDto is just a wrapper for a String, or use RequestBody directly.
    // Creating a simple DTO for the note:
    // package com.dreamcollections.services.order.dto;
    // import lombok.Data; @Data public class AdminNoteDto { private String noteText; }
    // For now, using a simple @RequestParam or direct @RequestBody String for simplicity if no DTO is created.
    // Let's use a simple DTO for clarity.

    // Define AdminNoteDto inline for this example, or create it as a separate file.
    // For the purpose of this step, I'll assume AdminNoteDto is simple:
    // static class AdminNoteRequest { public String noteText; }

    @PostMapping("/{orderId}/notes")
    public ResponseEntity<AdminOrderDetailDto> addAdminNoteToOrder(
            @PathVariable Long orderId,
            @RequestBody String noteText, // Simple string request body for the note
            @RequestHeader(name = "X-Admin-Username", required = false, defaultValue = "UnknownAdmin") String adminUsername
             // String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName(); // Better way
    ) {
        log.info("Admin request: Add note to order ID {} by admin {}. Note: {}", orderId, adminUsername, noteText);
        if (noteText == null || noteText.trim().isEmpty()) {
            // Consider if empty notes are allowed or should be a bad request
            // For now, assuming service layer handles empty notes if necessary.
        }
        AdminOrderDetailDto updatedOrder = orderService.addNoteToOrder(orderId, noteText.trim(), adminUsername);
        return ResponseEntity.ok(updatedOrder);
    }
}
