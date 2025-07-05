package com.dreamcollections.services.order.service.impl;

import com.dreamcollections.services.order.client.CartServiceClient;
import com.dreamcollections.services.order.client.ProductCatalogServiceClient;
import com.dreamcollections.services.order.dto.AddressDto;
import com.dreamcollections.services.order.dto.AdminOrderDetailDto;
import com.dreamcollections.services.order.dto.AdminOrderSummaryDto;
import com.dreamcollections.services.order.dto.client.CartDataDto;
import com.dreamcollections.services.order.dto.client.CartItemForOrderDto;
import com.dreamcollections.services.order.dto.client.ProductVariantDetailDto;
import com.dreamcollections.services.order.dto.client.UpdateStockRequestDto;
import com.dreamcollections.services.order.dto.request.CreateOrderRequestDto;
import com.dreamcollections.services.order.dto.response.OrderItemResponseDto;
import com.dreamcollections.services.order.dto.response.OrderResponseDto;
import com.dreamcollections.services.order.exception.BadRequestException;
import com.dreamcollections.services.order.exception.ResourceNotFoundException;
import com.dreamcollections.services.order.model.Address;
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
        Address shippingAddressEntity = mapAddressDtoToEntity(createOrderRequestDto.getShippingAddress());
        Address billingAddressEntity = mapAddressDtoToEntity(createOrderRequestDto.getBillingAddress());

        // If billingAddress is not provided in request, and business rule is to use shipping address as billing:
        if (billingAddressEntity == null && shippingAddressEntity != null) {
            // This creates a distinct Address object for billing, even if data is same.
            // If they should truly point to the same embedded instance (sharing columns if not for @AttributeOverrides),
            // then Order entity design might need adjustment or just pass shippingAddressEntity for both.
            // For distinct columns as set up by @AttributeOverrides, creating a new instance or copying is fine.
            billingAddressEntity = shippingAddressEntity; // Or create a new Address object with same values
        }


        Order order = new Order(
                userId,
                createOrderRequestDto.getCustomerEmail(),
                createOrderRequestDto.getCustomerNameSnapshot(),
                calculatedTotalAmount,
                OrderStatus.PENDING_PAYMENT, // Initial order status
                com.dreamcollections.services.order.model.PaymentStatus.PENDING, // Initial payment status
                shippingAddressEntity,
                billingAddressEntity,
                createOrderRequestDto.getPaymentMethod()
        );
        // Set optional fields if provided
        if (createOrderRequestDto.getShippingMethod() != null && !createOrderRequestDto.getShippingMethod().isEmpty()) {
            order.setShippingMethod(createOrderRequestDto.getShippingMethod());
        }

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

    // --- Helper to map AddressDto to Address entity ---
    private Address mapAddressDtoToEntity(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }
        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setCity(addressDto.getCity());
        address.setStateOrProvince(addressDto.getStateOrProvince());
        address.setPostalCode(addressDto.getPostalCode());
        address.setCountry(addressDto.getCountry());
        address.setContactPhone(addressDto.getContactPhone());
        return address;
    }

    // --- Helper to map Address entity to AddressDto ---
    private AddressDto mapAddressEntityToDto(Address addressEntity) {
        if (addressEntity == null) {
            return null;
        }
        AddressDto addressDto = new AddressDto();
        addressDto.setStreet(addressEntity.getStreet());
        addressDto.setAddressLine2(addressEntity.getAddressLine2());
        addressDto.setCity(addressEntity.getCity());
        addressDto.setStateOrProvince(addressEntity.getStateOrProvince());
        addressDto.setPostalCode(addressEntity.getPostalCode());
        addressDto.setCountry(addressEntity.getCountry());
        addressDto.setContactPhone(addressEntity.getContactPhone());
        return addressDto;
    }


    // --- Admin DTO Mappers ---

    private AdminOrderSummaryDto mapOrderToAdminSummaryDto(Order order) {
        if (order == null) return null;
        return new AdminOrderSummaryDto(
                order.getId(),
                order.getUserId(),
                order.getCustomerEmail(),
                order.getCustomerNameSnapshot(),
                order.getCreatedAt(), // Using createdAt as orderDate for summary
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getItems() != null ? order.getItems().size() : 0
        );
    }

    private AdminOrderDetailDto mapOrderToAdminDetailDto(Order order) {
        if (order == null) return null;

        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
                .map(this::mapOrderItemToDto) // Reuse existing item mapper
                .collect(Collectors.toList());

        AddressDto shippingAddressDto = mapAddressEntityToDto(order.getShippingAddress());
        AddressDto billingAddressDto = mapAddressEntityToDto(order.getBillingAddress());

        // Assuming OrderStatusLogDto would be created and mapped if statusHistory is implemented
        // List<OrderStatusLogDto> statusHistoryDto = order.getStatusLogs().stream().map(this::mapStatusLogToDto).collect(Collectors.toList());


        return new AdminOrderDetailDto(
                order.getId(),
                order.getUserId(),
                order.getCustomerEmail(),
                order.getCustomerNameSnapshot(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getTotalAmount(),
                order.getStatus().name(),
                itemDtos,
                shippingAddressDto,
                billingAddressDto,
                order.getPaymentMethod(),
                order.getPaymentStatus().name(), // Assuming PaymentStatus is an enum
                order.getShippingMethod(),
                order.getTrackingNumber()
                // statusHistoryDto // if implemented
        );
    }


    // --- Admin Service Method Implementations ---

    @Override
    @Transactional(readOnly = true)
    public Page<AdminOrderSummaryDto> getAllOrdersForAdmin(Specification<Order> spec, Pageable pageable) {
        log.debug("Admin request: Fetching all orders with spec. Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return orderRepository.findAll(spec, pageable).map(this::mapOrderToAdminSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AdminOrderDetailDto> getOrderDetailsForAdmin(Long orderId) {
        log.debug("Admin request: Fetching details for order ID {}.", orderId);
        // Use findByIdWithItems to ensure items are fetched, or rely on OpenSessionInView/transactional context for LAZY loading
        return orderRepository.findByIdWithItems(orderId).map(this::mapOrderToAdminDetailDto);
    }

    @Override
    @Transactional
    public AdminOrderDetailDto updateOrderStatus(Long orderId, String newStatusStr, String adminUsername) {
        log.info("Admin {} request: Updating status for order ID {} to {}.", adminUsername, orderId, newStatusStr);
        Order order = orderRepository.findByIdWithItems(orderId) // Fetch with items for DTO mapping
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status string provided: {}", newStatusStr);
            throw new BadRequestException("Invalid order status provided: " + newStatusStr);
        }

        OrderStatus oldStatus = order.getStatus();

        if (oldStatus == newStatus) {
            log.info("Order {} already in status {}. No change.", orderId, newStatus);
            return mapOrderToAdminDetailDto(order);
        }

        if (!isValidTransition(oldStatus, newStatus)) {
            log.warn("Invalid status transition for order ID {}: from {} to {}", orderId, oldStatus, newStatus);
            throw new BadRequestException("Invalid status transition from " + oldStatus + " to " + newStatus + ".");
        }

        order.setStatus(newStatus);

        // Log the status change
        String notes = "Status changed by admin: " + adminUsername;
        OrderStatusLog statusLog = new OrderStatusLog(order, oldStatus, newStatus, adminUsername, notes);
        order.addStatusLog(statusLog); // Assumes Order entity has addStatusLog method

        Order updatedOrder = orderRepository.save(order);
        log.info("Order ID {} status updated to {} by admin {}.", updatedOrder.getId(), newStatus, adminUsername);

        // TODO: Publish OrderStatusChangedEvent if needed for other services/notifications
        // Example: if (newStatus == OrderStatus.PAID) { eventPublisher.publish(new OrderPaidEvent(order.getId())); }
        // else if (newStatus == OrderStatus.SHIPPED) { eventPublisher.publish(new OrderShippedEvent(order.getId(), order.getTrackingNumber())); }


        return mapOrderToAdminDetailDto(updatedOrder);
    }

    private static final Map<OrderStatus, Set<OrderStatus>> validTransitions = new HashMap<>();

    static {
        // From PENDING_PAYMENT
        validTransitions.put(OrderStatus.PENDING_PAYMENT, Set.of(
                OrderStatus.AWAITING_PAYMENT_CONFIRMATION,
                OrderStatus.PAID, // Direct if payment is fast/integrated
                OrderStatus.CANCELLED,
                OrderStatus.FAILED
        ));
        // From AWAITING_PAYMENT_CONFIRMATION
        validTransitions.put(OrderStatus.AWAITING_PAYMENT_CONFIRMATION, Set.of(
                OrderStatus.PAID,
                OrderStatus.FAILED,
                OrderStatus.CANCELLED // If user cancels or system times out
        ));
        // From PAID
        validTransitions.put(OrderStatus.PAID, Set.of(
                OrderStatus.PROCESSING,
                OrderStatus.REFUND_PENDING, // If a refund is requested immediately after payment
                OrderStatus.CANCELLED // If order can be cancelled before processing
        ));
        // From PROCESSING
        validTransitions.put(OrderStatus.PROCESSING, Set.of(
                OrderStatus.SHIPPED,
                OrderStatus.CANCELLED, // If cancellation is allowed during processing
                OrderStatus.REFUND_PENDING // If processed but then needs refund before shipping
        ));
        // From SHIPPED
        validTransitions.put(OrderStatus.SHIPPED, Set.of(
                OrderStatus.DELIVERED,
                OrderStatus.REFUND_PENDING // For returns after shipping
        ));
        // From DELIVERED
        validTransitions.put(OrderStatus.DELIVERED, Set.of(
                OrderStatus.REFUND_PENDING // For returns after delivery
        ));
        // From CANCELLED - Generally a terminal state for admin changes, but could allow re-opening if needed (not typical)
         validTransitions.put(OrderStatus.CANCELLED, Set.of()); // No transitions out by admin by default

        // From REFUND_PENDING
        validTransitions.put(OrderStatus.REFUND_PENDING, Set.of(
                OrderStatus.REFUNDED,
                OrderStatus.FAILED // If refund attempt fails
        ));
        // From REFUNDED - Terminal state
        validTransitions.put(OrderStatus.REFUNDED, Set.of());

        // From FAILED - Could potentially be retried or moved to cancelled
        validTransitions.put(OrderStatus.FAILED, Set.of(
                OrderStatus.CANCELLED,
                OrderStatus.PENDING_PAYMENT // If admin wants to re-initiate or allow user to retry
        ));
    }

    private boolean isValidTransition(OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus == null) { // Should not happen for an existing order
            return false;
        }
        Set<OrderStatus> allowedTransitions = validTransitions.get(oldStatus);
        return allowedTransitions != null && allowedTransitions.contains(newStatus);
    }


    @Override
    @Transactional
    public AdminOrderDetailDto addNoteToOrder(Long orderId, String noteText, String adminUsername) {
        log.info("Admin {} request: Adding note to order ID {}.", adminUsername, orderId);
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // For now, admin notes are part of OrderStatusLog.
        // This method could create a specific type of log entry or update a dedicated notes field if one existed on Order.
        // Let's create a "NOTE" type status log or similar. This interpretation might need refinement.
        // A simpler approach for general notes might be a dedicated List<AdminNote> on Order entity.
        // For now, we'll log it as a generic status log entry with current status as previous and new.
        // This is a bit of a workaround if a dedicated notes field isn't on Order.

        OrderStatusLog noteLog = new OrderStatusLog(order, order.getStatus(), order.getStatus(), adminUsername, "Admin Note: " + noteText);
        order.addStatusLog(noteLog);

        Order updatedOrder = orderRepository.save(order);
        log.info("Admin note added to order ID {} by {}.", updatedOrder.getId(), adminUsername);
        return mapOrderToAdminDetailDto(updatedOrder);
    }
}
