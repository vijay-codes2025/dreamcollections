package com.dreamcollections.services.product.repository;

import com.dreamcollections.services.product.model.Product;
// import com.dreamcollections.services.product.model.Category; // Not strictly needed if using findByCategoryId
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Fetch product with variants and category eagerly for detail views
    @Query("SELECT p FROM Product p JOIN FETCH p.category LEFT JOIN FETCH p.variants WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(Long id);

    // For listing, default lazy loading is fine, or specific projections can be used.
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Find products by category ID including all subcategories (hierarchical filtering)
    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    Page<Product> findByCategoryIdIn(List<Long> categoryIds, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
