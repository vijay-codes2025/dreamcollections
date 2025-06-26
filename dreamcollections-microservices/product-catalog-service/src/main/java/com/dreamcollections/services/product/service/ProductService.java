package com.dreamcollections.services.product.service;

import com.dreamcollections.services.product.dto.ProductRequestDto;
import com.dreamcollections.services.product.dto.ProductResponseDto;
import com.dreamcollections.services.product.dto.ProductVariantDto; // For stock update
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto productRequestDto);
    Optional<ProductResponseDto> getProductById(Long id);
    Page<ProductResponseDto> getAllProducts(Pageable pageable);
    Page<ProductResponseDto> getProductsByCategoryId(Long categoryId, Pageable pageable);
    Page<ProductResponseDto> searchProductsByName(String name, Pageable pageable);
    ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto);
    void deleteProduct(Long id);

    // Stock Management
    ProductVariantDto updateStock(Long productVariantId, Integer newStockQuantity);
    Optional<ProductVariantDto> getProductVariantById(Long productVariantId);
    List<ProductVariantDto> getProductVariantsByIds(List<Long> variantIds); // For cart/order service
}
