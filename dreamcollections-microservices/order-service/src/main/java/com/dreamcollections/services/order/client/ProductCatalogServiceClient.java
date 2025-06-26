package com.dreamcollections.services.order.client;

import com.dreamcollections.services.order.dto.client.ProductVariantDetailDto;
import com.dreamcollections.services.order.dto.client.UpdateStockRequestDto; // Renamed from Product Catalog's DTO to avoid confusion
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Name matches the application name of product-catalog-service for service discovery
// URL is for direct connection during local dev or when service discovery is not used
@FeignClient(name = "product-catalog-service", url = "${services.product-catalog.url}")
public interface ProductCatalogServiceClient {

    // Get details for multiple product variants (e.g., for all items in a cart)
    // This endpoint should exist in ProductCatalogService's ProductController
    @PostMapping("/products/variants/findByIds") // Ensure path matches ProductCatalogService endpoint
    List<ProductVariantDetailDto> getProductVariantsByIds(@RequestBody List<Long> variantIds);

    // Update stock for a specific product variant
    // This endpoint should exist in ProductCatalogService's ProductController
    @PutMapping("/products/variants/{variantId}/stock")
    ProductVariantDetailDto updateStock(
            @PathVariable("variantId") Long variantId,
            @RequestBody UpdateStockRequestDto stockRequest); // DTO for updating stock
}
