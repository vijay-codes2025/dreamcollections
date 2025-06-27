package com.dreamcollections.services.cart.service.impl;

import com.dreamcollections.services.cart.client.ProductCatalogServiceClient;
import com.dreamcollections.services.cart.dto.CartDto;
import com.dreamcollections.services.cart.dto.CartItemDto;
import com.dreamcollections.services.cart.dto.ProductVariantDetailDto;
import com.dreamcollections.services.cart.exception.BadRequestException; // Need this
import com.dreamcollections.services.cart.exception.ResourceNotFoundException; // Need this
import com.dreamcollections.services.cart.model.Cart;
import com.dreamcollections.services.cart.model.CartItem;
import com.dreamcollections.services.cart.payload.request.AddItemToCartRequestDto;
import com.dreamcollections.services.cart.payload.request.UpdateCartItemQuantityRequestDto;
import com.dreamcollections.services.cart.repository.CartItemRepository;
import com.dreamcollections.services.cart.repository.CartRepository;
import com.dreamcollections.services.cart.service.CartService;

import feign.FeignException; // To handle errors from Feign client
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductCatalogServiceClient productCatalogServiceClient;

    // --- Helper Methods ---
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            log.info("No cart found for user ID {}. Creating a new cart.", userId);
            Cart newCart = new Cart(userId);
            return cartRepository.save(newCart);
        });
    }

    private CartDto convertCartToDto(Cart cart) {
        if (cart == null) {
            return null;
        }
        List<CartItemDto> itemDtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cart.getItems())) {
            // Fetch product variant details for all items in batch
            List<Long> variantIds = cart.getItems().stream()
                                        .map(CartItem::getProductVariantId)
                                        .distinct()
                                        .collect(Collectors.toList());

            Map<Long, ProductVariantDetailDto> variantDetailsMap = new HashMap<>();
            if (!variantIds.isEmpty()) {
                try {
                    log.debug("Fetching variant details from product-catalog-service for IDs: {}", variantIds);
                    List<ProductVariantDetailDto> fetchedVariants = productCatalogServiceClient.getProductVariantsByIds(variantIds);
                    variantDetailsMap = fetchedVariants.stream()
                                           .collect(Collectors.toMap(ProductVariantDetailDto::getId, Function.identity()));
                    log.debug("Fetched {} variant details.", fetchedVariants.size());
                } catch (FeignException e) {
                    log.error("Error fetching variant details from product-catalog-service. Status: {}, Body: {}", e.status(), e.contentUTF8(), e);
                    // Decide how to handle: throw exception, or return cart with partial/missing item details?
                    // For now, let's proceed and items without details will have nulls.
                }
            }


            for (CartItem item : cart.getItems()) {
                ProductVariantDetailDto variantDetail = variantDetailsMap.get(item.getProductVariantId());
                if (variantDetail != null) {
                    itemDtos.add(new CartItemDto(
                        item.getId(),
                        item.getProductVariantId(),
                        variantDetail.getProductName(),
                        variantDetail.getProductImageUrl(),
                        variantDetail.getSize(),
                        variantDetail.getProductPrice(), // This is the current price from catalog
                        item.getQuantity()
                    ));
                } else {
                    log.warn("Could not find product variant details for ID {} in cart for user {}. Item will be incomplete in DTO.", item.getProductVariantId(), cart.getUserId());
                    // Add item with minimal info or skip? Let's add with what we have.
                     itemDtos.add(new CartItemDto(
                        item.getId(),
                        item.getProductVariantId(),
                        "N/A (Product info unavailable)",
                        null, // no image
                        "N/A",
                        null, // no price
                        item.getQuantity()
                    ));
                }
            }
        }
        CartDto cartDto = new CartDto(cart.getId(), cart.getUserId(), itemDtos);
        // cartDto.recalculateTotals(); // Constructor should handle this
        return cartDto;
    }


    // --- Service Methods ---
    @Override
    @Transactional(readOnly = true)
    public CartDto getCartByUserId(Long userId) {
        log.debug("Getting cart for user ID: {}", userId);
        Cart cart = getOrCreateCart(userId);
        return convertCartToDto(cart);
    }

    @Override
    @Transactional
    public CartDto addItemToCart(Long userId, AddItemToCartRequestDto addItemDto) {
        log.info("Adding item to cart for user ID {}: VariantID {}, Quantity {}", userId, addItemDto.getProductVariantId(), addItemDto.getQuantity());
        Cart cart = getOrCreateCart(userId);

        ProductVariantDetailDto variantDetail;
        try {
            log.debug("Fetching product variant {} details from product-catalog-service.", addItemDto.getProductVariantId());
            variantDetail = productCatalogServiceClient.getProductVariantById(addItemDto.getProductVariantId());
        } catch (FeignException e) {
            log.error("FeignException while fetching product variant {}: Status {}, Body {}", addItemDto.getProductVariantId(), e.status(), e.contentUTF8(), e);
            if (e.status() == 404) {
                throw new ResourceNotFoundException("ProductVariant", "id", addItemDto.getProductVariantId());
            }
            throw new BadRequestException("Could not retrieve product details. Error: " + e.getMessage());
        }

        if (variantDetail == null) { // Should be caught by Feign 404 generally
             throw new ResourceNotFoundException("ProductVariant", "id", addItemDto.getProductVariantId());
        }

        if (variantDetail.getStockQuantity() < addItemDto.getQuantity()) {
            throw new BadRequestException("Not enough stock for product: " + variantDetail.getProductName() + " - " + variantDetail.getSize() +
                                          ". Available: " + variantDetail.getStockQuantity() + ", Requested: " + addItemDto.getQuantity());
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProductVariantId(cart, addItemDto.getProductVariantId());

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            log.debug("Variant {} already in cart. Updating quantity.", addItemDto.getProductVariantId());
            int newQuantity = cartItem.getQuantity() + addItemDto.getQuantity();
            if (variantDetail.getStockQuantity() < newQuantity) {
                 throw new BadRequestException("Not enough stock to add more of product: " + variantDetail.getProductName() + " - " + variantDetail.getSize() +
                                          ". Available: " + variantDetail.getStockQuantity() + ", Current in cart: " + cartItem.getQuantity() + ", Requested additional: " + addItemDto.getQuantity());
            }
            cartItem.setQuantity(newQuantity);
        } else {
            log.debug("Variant {} not in cart. Adding new item.", addItemDto.getProductVariantId());
            cartItem = new CartItem(cart, addItemDto.getProductVariantId(), addItemDto.getQuantity());
            cart.addItem(cartItem); // Adds to set and sets bidirectional link
        }
        cartItemRepository.save(cartItem); // Save CartItem
        // cartRepository.save(cart); // Not strictly necessary if only CartItem changes and Cart itself doesn't (e.g. no total fields on Cart entity)
                                    // But good practice if Cart's updatedAt field needs to be triggered.
        log.info("Item {} updated/added to cart for user {}.", addItemDto.getProductVariantId(), userId);
        return convertCartToDto(cartRepository.findById(cart.getId()).orElse(cart)); // Re-fetch cart to get fresh state
    }

    @Override
    @Transactional
    public CartDto updateCartItemQuantity(Long userId, Long productVariantId, UpdateCartItemQuantityRequestDto updateQuantityDto) {
        log.info("Updating cart item for user ID {}: VariantID {}, New Quantity {}", userId, productVariantId, updateQuantityDto.getQuantity());
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findByCartAndProductVariantId(cart, productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found for product variant id: " + productVariantId + " in user's cart."));

        if (updateQuantityDto.getQuantity() <= 0) {
            log.debug("Quantity is 0 or less. Removing item {} from cart for user {}.", productVariantId, userId);
            cart.removeItem(cartItem); // Manages collection in Cart entity
            cartItemRepository.delete(cartItem);
        } else {
            ProductVariantDetailDto variantDetail;
            try {
                log.debug("Fetching product variant {} details for stock check.", productVariantId);
                variantDetail = productCatalogServiceClient.getProductVariantById(productVariantId);
            } catch (FeignException e) {
                 log.error("FeignException while fetching product variant {}: Status {}, Body {}", productVariantId, e.status(), e.contentUTF8(), e);
                 if (e.status() == 404) throw new ResourceNotFoundException("ProductVariant", "id", productVariantId);
                 throw new BadRequestException("Could not retrieve product details for stock check. Error: " + e.getMessage());
            }
             if (variantDetail == null) throw new ResourceNotFoundException("ProductVariant", "id", productVariantId);


            if (variantDetail.getStockQuantity() < updateQuantityDto.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + variantDetail.getProductName() + " - " + variantDetail.getSize() +
                                              ". Available: " + variantDetail.getStockQuantity() + ", Requested: " + updateQuantityDto.getQuantity());
            }
            cartItem.setQuantity(updateQuantityDto.getQuantity());
            cartItemRepository.save(cartItem);
            log.debug("Quantity for item {} updated to {} for user {}.", productVariantId, updateQuantityDto.getQuantity(), userId);
        }
        // cartRepository.save(cart); // Again, potentially to update Cart's timestamp or if it has derived fields
        return convertCartToDto(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Override
    @Transactional
    public CartDto removeItemFromCart(Long userId, Long productVariantId) {
        log.info("Removing item VariantID {} from cart for user ID {}", productVariantId, userId);
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findByCartAndProductVariantId(cart, productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found for product variant id: " + productVariantId + " in user's cart."));

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        log.debug("Item {} removed from cart for user {}.", productVariantId, userId);
        // cartRepository.save(cart);
        return convertCartToDto(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing cart for user ID {}", userId);
        Cart cart = getOrCreateCart(userId);
        if (!CollectionUtils.isEmpty(cart.getItems())) {
            // cartItemRepository.deleteAll(cart.getItems()); // Alternative to orphanRemoval or explicit iteration
            // cart.getItems().clear(); // This should trigger orphanRemoval if Cart entity owns CartItems fully
            // For safety, explicit delete of items then clear from cart's collection
            List<CartItem> itemsToDelete = new ArrayList<>(cart.getItems());
            cart.getItems().clear(); // Clear from parent side
            cartItemRepository.deleteAllInBatch(itemsToDelete); // Efficient delete
        }
        // cartRepository.save(cart); // Save cart (e.g., to update timestamp)
        log.debug("Cart cleared for user {}.", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getCartForCheckout(Long userId) {
        // This might involve additional checks or a "snapshot" of the cart
        // For now, it's the same as getCartByUserId
        log.debug("Getting cart for checkout for user ID: {}", userId);
        return getCartByUserId(userId);
    }
}
