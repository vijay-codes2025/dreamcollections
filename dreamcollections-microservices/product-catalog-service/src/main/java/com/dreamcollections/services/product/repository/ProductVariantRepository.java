package com.dreamcollections.services.product.repository;

import com.dreamcollections.services.product.model.ProductVariant;
// import com.dreamcollections.services.product.model.Product; // Not strictly needed if using findByProductId
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);
    Optional<ProductVariant> findByProductIdAndSize(Long productId, String size);
    // Add method to find multiple variants by ID list if needed for cart/order service.
    List<ProductVariant> findByIdIn(List<Long> ids);
}
