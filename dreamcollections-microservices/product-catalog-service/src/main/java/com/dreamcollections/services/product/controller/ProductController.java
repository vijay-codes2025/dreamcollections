package com.dreamcollections.services.product.controller;

import com.dreamcollections.services.product.dto.ProductRequestDto;
import com.dreamcollections.services.product.dto.ProductResponseDto;
import com.dreamcollections.services.product.dto.ProductVariantDto; // For stock update response
import com.dreamcollections.services.product.dto.UpdateStockRequestDto; // New DTO for stock update
import com.dreamcollections.services.product.payload.response.MessageResponse;
import com.dreamcollections.services.product.service.ProductService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List; // For getProductVariantsByIds
import java.util.Optional;

@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/products") // Path within this service. API Gateway maps /api/catalog/products here
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {
        log.info("Request to create product: {}", productRequestDto.getName());
        ProductResponseDto createdProduct = productService.createProduct(productRequestDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        log.debug("Request to get product by ID: {}", id);
        Optional<ProductResponseDto> productDto = productService.getProductById(id);
        return productDto.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        log.debug("Request to get all products. Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ProductResponseDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponseDto>> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        log.debug("Request to get products for category ID: {}. Page: {}, Size: {}", categoryId, pageable.getPageNumber(), pageable.getPageSize());
        Page<ProductResponseDto> products = productService.getProductsByCategoryId(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> searchProductsByName(
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        log.debug("Request to search products by name: '{}'. Page: {}, Size: {}", name, pageable.getPageNumber(), pageable.getPageSize());
        Page<ProductResponseDto> products = productService.searchProductsByName(name, pageable);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDto productRequestDto) {
        log.info("Request to update product ID {}: {}", id, productRequestDto.getName());
        ProductResponseDto updatedProduct = productService.updateProduct(id, productRequestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
        log.info("Request to delete product ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(new MessageResponse("Product deleted successfully!"));
    }

    // --- Product Variant Specific Endpoints ---
    @GetMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariantDto> getProductVariantById(@PathVariable Long variantId) {
        log.debug("Request to get product variant by ID: {}", variantId);
        // This endpoint might be used by other services (e.g. CartService, OrderService)
        // or for admin purposes.
        return productService.getProductVariantById(variantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/variants/findByIds") // Using POST for request body with list of IDs
    public ResponseEntity<List<ProductVariantDto>> getProductVariantsByIds(@RequestBody List<Long> variantIds) {
        log.debug("Request to get product variants by IDs: {}", variantIds);
        List<ProductVariantDto> variants = productService.getProductVariantsByIds(variantIds);
        if (variants.isEmpty() && !variantIds.isEmpty()) {
            // Optional: return 404 if none of the requested variants were found,
            // or 200 with empty list if some could be valid but not found.
            // For simplicity, 200 with (potentially empty) list is fine.
        }
        return ResponseEntity.ok(variants);
    }


    @PutMapping("/variants/{variantId}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORDER_SERVICE')") // Example, ORDER_SERVICE role for service-to-service
    public ResponseEntity<ProductVariantDto> updateStock(
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateStockRequestDto stockRequest) {
        log.info("Request to update stock for variant ID {}: new quantity {}", variantId, stockRequest.getStockQuantity());
        ProductVariantDto updatedVariant = productService.updateStock(variantId, stockRequest.getStockQuantity());
        return ResponseEntity.ok(updatedVariant);
    }
}
