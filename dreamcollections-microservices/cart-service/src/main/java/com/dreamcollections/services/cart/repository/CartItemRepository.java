package com.dreamcollections.services.cart.repository;

import com.dreamcollections.services.cart.model.Cart;
import com.dreamcollections.services.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductVariantId(Cart cart, Long productVariantId);
    void deleteByCartAndProductVariantId(Cart cart, Long productVariantId); // For removing specific items
}
