package com.dreamcollections.services.order.service;

import com.dreamcollections.services.order.dto.AdminOrderDetailDto;
import com.dreamcollections.services.order.dto.AdminOrderSummaryDto;
// import com.dreamcollections.services.order.dto.OrderStatusUpdateDto; // This DTO is for request, service method might just take string
import com.dreamcollections.services.order.dto.request.CreateOrderRequestDto;
import com.dreamcollections.services.order.dto.response.OrderResponseDto;
import com.dreamcollections.services.order.model.Order; // For Specification
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // Import for Specification

import java.util.Optional;

public interface OrderService {

    // --- Customer Facing Methods ---
    OrderResponseDto createOrder(Long userId, CreateOrderRequestDto createOrderRequestDto);
    Optional<OrderResponseDto> getOrderByIdAndUserId(Long orderId, Long userId);
    Page<OrderResponseDto> getOrdersByUserId(Long userId, Pageable pageable);

    // --- Admin Facing Methods ---
    /**
     * Retrieves a paginated list of orders for administrators, allowing filtering.
     * @param spec Specification for dynamic filtering (e.g., by status, date range, customer).
     * @param pageable Pagination information.
     * @return A page of {@link AdminOrderSummaryDto}.
     */
    Page<AdminOrderSummaryDto> getAllOrdersForAdmin(Specification<Order> spec, Pageable pageable);

    /**
     * Retrieves detailed information for a specific order for administrators.
     * @param orderId The ID of the order.
     * @return An Optional containing {@link AdminOrderDetailDto} if found, otherwise empty.
     */
    Optional<AdminOrderDetailDto> getOrderDetailsForAdmin(Long orderId);

    /**
     * Updates the status of an order.
     * @param orderId The ID of the order to update.
     * @param newStatus The new status string (should match OrderStatus enum values).
     * @param adminUsername The username of the admin performing the action (for auditing).
     * @return The updated {@link AdminOrderDetailDto} with the new status.
     * @throws com.dreamcollections.services.order.exception.ResourceNotFoundException if order not found.
     * @throws IllegalArgumentException if status transition is invalid.
     */
    AdminOrderDetailDto updateOrderStatus(Long orderId, String newStatus, String adminUsername);

    /**
     * Adds an administrative note to an order. (Optional feature from plan)
     * @param orderId The ID of the order.
     * @param noteText The text of the note.
     * @param adminUsername The username of the admin adding the note.
     * @return The updated {@link AdminOrderDetailDto} with the new note.
     * @throws com.dreamcollections.services.order.exception.ResourceNotFoundException if order not found.
     */
    AdminOrderDetailDto addNoteToOrder(Long orderId, String noteText, String adminUsername);
}
