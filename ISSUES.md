# Tracked Issues

This file lists actionable TODO comments found in the repository.
Each issue has an identifier that can be referenced from code comments.

## Issue 1
**Location:** `identity-service` - `AuthController.java` line 118
**Description:** Publish `UserCreatedEvent` or call CartService when a user registers. This will synchronize user creation with cart creation.

## Issue 2
**Location:** `identity-service` - `SecurityConfig.java` line 64
**Description:** Restrict allowed origins in CORS configuration for production environments.

## Issue 3
**Location:** `product-catalog-service` - `ProductController.java` import section
**Description:** Security annotations already exist; remove the outdated `TODO` comment.

## Issue 4
**Location:** `product-catalog-service` - `SecurityConfig.java` lines 38-47 (commented block)
**Description:** Restrict CORS configuration in production instead of using wildcard.

## Issue 5
**Location:** `order-service` - `OrderServiceImpl.java` line 57
**Description:** Inject and configure RabbitMQ template to publish order-related events.

## Issue 6
**Location:** `order-service` - `OrderServiceImpl.java` line 236
**Description:** Publish `OrderPlacedEvent` after successful order creation.

## Issue 7
**Location:** `order-service` - `OrderServiceImpl.java` line 396
**Description:** Publish `OrderStatusChangedEvent` when order status updates require notification to other services.

