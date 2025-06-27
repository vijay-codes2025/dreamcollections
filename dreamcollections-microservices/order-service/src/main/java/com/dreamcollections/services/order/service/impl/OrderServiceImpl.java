package com.dreamcollections.services.order.service.impl;

import com.dreamcollections.services.order.client.CartServiceClient;
import com.dreamcollections.services.order.client.ProductCatalogServiceClient;
import com.dreamcollections.services.order.dto.client.CartDataDto;
import com.dreamcollections.services.order.dto.client.CartItemForOrderDto;
import com.dreamcollections.services.order.dto.client.ProductVariantDetailDto;
import com.dreamcollections.services.order.dto.client.UpdateStockRequestDto;
import com.dreamcollections.services.order.dto.request.CreateOrderRequestDto;
import com.dreamcollections.services.order.dto.response.OrderItemResponseDto;
import com.dreamcollections.services.order.dto.response.OrderResponseDto;
import com.dreamcollections.services.order.exception.BadRequestException; // Need this
import com.dreamcollections.services.order.exception.ResourceNotFoundException; // Need this
import com.dreamcollections.services.order.model.Order;
import com.dreamcollections.services.order.model.OrderItem;
import com.dreamcollections.services.order.model.OrderStatus;
import com.dreamcollections.services.order.repository.OrderRepository;
import com.dreamcollections.services.order.service.OrderService;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartServiceClient cartServiceClient;

    @Autowired
    private ProductCatalogServiceClient productCatalogServiceClient;

    // TODO: Inject RabbitMQ template for event publishing later
    // @Autowired
    // private RabbitTemplate rabbitTemplate;
    // @Value("${app.rabbitmq.exchange.orderEvents}")
    // private String orderEventsExchange;
    // @Value("${app.rabbitmq.routingKey.orderPlaced}")
    // private String orderPlacedRoutingKey;


    // --- Mapper Methods ---
    private OrderItemResponseDto mapOrderItemToDto(OrderItem item) {
        return new OrderItemResponseDto(
            item.getId(),
            item.getProductVariantId(),
            item.getProductName(),
            item.getVariantSize(),
            item.getProductImageUrl(),
            item.getQuantity(),
            item.getPriceAtPurchase()
        );
    }

    private OrderResponseDto mapOrderToDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
            .map(this::mapOrderItemToDto)
            .collect(Collectors.toList());

        return new OrderResponseDto(
            order.getId(),
            order.getUserId(),
            itemDtos,
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            order.getShippingAddress(),
            order.getPaymentTransactionId()
        );
    }

    // --- Service Implementation ---

    @Override
    @Transactional // This method involves multiple operations, should be transactional
    public OrderResponseDto createOrder(Long userId, CreateOrderRequestDto createOrderRequestDto) {
        log.info("Attempting to create order for user ID: {}", userId);

        // 1. Fetch Cart from Cart Service
        CartDataDto cartData;
        try {
            log.debug("Fetching cart for user ID {} from cart-service.", userId);
            cartData = cartServiceClient.getUserCart(); // Assumes JWT propagation handles user context
            if (cartData == null || CollectionUtils.isEmpty(cartData.getItems())) {
                log.warn("Cart is empty or null for user ID {}. Cannot create order.", userId);
                throw new BadRequestException("Cannot create order from an empty cart.");
            }
        } catch (FeignException e) {
            log.error("Error fetching cart for user ID {}: Status {}, Body {}", userId, e.status(), e.contentUTF8(), e);
            throw new BadRequestException("Failed to retrieve cart details: " + e.getMessage());
        }

        // 2. Fetch Product Details (including current price and stock) from Product Catalog Service
        //    for each item in the cart to ensure data consistency and validate stock.
        List<Long> productVariantIds = cartData.getItems().stream()
                                        .map(CartItemForOrderDto::getProductVariantId)
                                        .collect(Collectors.toList());

        Map<Long, ProductVariantDetailDto> productVariantDetailsMap;
        try {
            log.debug("Fetching product variant details for IDs: {} from product-catalog-service.", productVariantIds);
            List<ProductVariantDetailDto> fetchedVariants = productCatalogServiceClient.getProductVariantsByIds(productVariantIds);
            productVariantDetailsMap = fetchedVariants.stream()
                                          .collect(Collectors.toMap(ProductVariantDetailDto::getId, Function.identity()));
            log.debug("Fetched {} product variant details.", fetchedVariants.size());
        } catch (FeignException e) {
            log.error("Error fetching product details for variant IDs {}: Status {}, Body {}", productVariantIds, e.status(), e.contentUTF8(), e);
            throw new BadRequestException("Failed to retrieve product details: " + e.getMessage());
        }

        // 3. Validate stock and prepare OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal calculatedTotalAmount = BigDecimal.ZERO;

        for (CartItemForOrderDto cartItem : cartData.getItems()) {
            ProductVariantDetailDto productDetail = productVariantDetailsMap.get(cartItem.getProductVariantId());
            if (productDetail == null) {
                log.error("Product details not found for variant ID {} from cart.", cartItem.getProductVariantId());
                throw new BadRequestException("Details for product variant ID " + cartItem.getProductVariantId() + " not found. Order cannot be processed.");
            }

            if (productDetail.getStockQuantity() < cartItem.getQuantity()) {
                log.warn("Insufficient stock for product variant ID {}: Name '{}', Size '{}'. Requested: {}, Available: {}",
                         productDetail.getId(), productDetail.getProductName(), productDetail.getSize(), cartItem.getQuantity(), productDetail.getStockQuantity());
                throw new BadRequestException("Insufficient stock for product: " + productDetail.getProductName() + " (" + productDetail.getSize() + ").");
            }

            OrderItem orderItem = new OrderItem(
                null, // Order will be set later after Order entity is created
                productDetail.getId(),
                productDetail.getProductName(),
                productDetail.getSize(),
                productDetail.getProductImageUrl(),
                cartItem.getQuantity(),
                productDetail.getProductPrice() // Price at the time of order creation
            );
            orderItems.add(orderItem);
            calculatedTotalAmount = calculatedTotalAmount.add(
                productDetail.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        // 4. Create and Save Order
        Order order = new Order(
            userId,
            calculatedTotalAmount,
            OrderStatus.PENDING_PAYMENT, // Initial status
            createOrderRequestDto.getShippingAddress()
        );
        orderItems.forEach(order::addItem); // Sets bidirectional relationship

        Order savedOrder = orderRepository.save(order);
        log.info("Order {} created successfully for user ID {} with status {}.", savedOrder.getId(), userId, savedOrder.getStatus());

        // 5. Update Stock in Product Catalog Service (critical step, consider resilience)
        // For now, direct REST call. Later, this could be event-driven.
        try {
            log.debug("Updating stock for order {}.", savedOrder.getId());
            for (OrderItem item : savedOrder.getItems()) {
                ProductVariantDetailDto productDetail = productVariantDetailsMap.get(item.getProductVariantId()); // Should still be in map
                int newStock = productDetail.getStockQuantity() - item.getQuantity();
                productCatalogServiceClient.updateStock(item.getProductVariantId(), new UpdateStockRequestDto(newStock));
                log.info("Stock updated for variant ID {}: new stock {}.", item.getProductVariantId(), newStock);
            }
        } catch (FeignException e) {
            log.error("Critical: Failed to update stock for order {}. Manual intervention may be required. Error: {}", savedOrder.getId(), e.getMessage(), e);
            // This is a critical failure. Options:
            // 1. Compensating transaction: Try to cancel/rollback the order (complex).
            // 2. Mark order as requiring attention.
            // 3. Rely on eventual consistency if using events.
            // For now, we log the error. The order is created, but stock might be inconsistent.
            // Consider changing order status to FAILED or AWAITING_STOCK_CONFIRMATION.
            savedOrder.setStatus(OrderStatus.FAILED); // Or a specific status indicating issues
            orderRepository.save(savedOrder); // Save updated status
            throw new BadRequestException("Order created, but failed to update product stock. Please contact support. Error: " + e.getMessage());
        }

        // 6. Clear Cart in Cart Service
        try {
            log.debug("Clearing cart for user ID {} after order {} creation.", userId, savedOrder.getId());
            cartServiceClient.clearUserCart(); // Assumes JWT propagation
            log.info("Cart cleared for user ID {}.", userId);
        } catch (FeignException e) {
            log.error("Failed to clear cart for user ID {} after order {}. Manual intervention may be needed. Error: {}", userId, savedOrder.getId(), e.getMessage(), e);
            // This is less critical than stock update, but still an issue. Log and continue.
        }

        // 7. TODO - Phase 3: Publish OrderPlacedEvent (e.g., to RabbitMQ)
        // OrderPlacedEvent event = new OrderPlacedEvent(savedOrder.getId(), userId, calculatedTotalAmount);
        // rabbitTemplate.convertAndSend(orderEventsExchange, orderPlacedRoutingKey, event);
        // log.info("OrderPlacedEvent published for order ID {}.", savedOrder.getId());

        return mapOrderToDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderResponseDto> getOrderByIdAndUserId(Long orderId, Long userId) {
        log.debug("Fetching order by ID {} for user ID {}.", orderId, userId);
        return orderRepository.findByIdAndUserIdWithItems(orderId, userId).map(this::mapOrderToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersByUserId(Long userId, Pageable pageable) {
        log.debug("Fetching orders for user ID {}. Page: {}, Size: {}", userId, pageable.getPageNumber(), pageable.getPageSize());
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::mapOrderToDto);
    }
}
