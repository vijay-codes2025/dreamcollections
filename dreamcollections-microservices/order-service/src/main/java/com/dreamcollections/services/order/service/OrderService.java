package com.dreamcollections.services.order.service;

import com.dreamcollections.services.order.dto.request.CreateOrderRequestDto;
import com.dreamcollections.services.order.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderService {
    OrderResponseDto createOrder(Long userId, CreateOrderRequestDto createOrderRequestDto);
    Optional<OrderResponseDto> getOrderByIdAndUserId(Long orderId, Long userId);
    Page<OrderResponseDto> getOrdersByUserId(Long userId, Pageable pageable);

    // Admin operations (optional for now, focus on user flow)
    // Optional<OrderResponseDto> getOrderByIdForAdmin(Long orderId);
    // Page<OrderResponseDto> getAllOrdersForAdmin(Pageable pageable);
    // OrderResponseDto updateOrderStatusForAdmin(Long orderId, String status);
}
