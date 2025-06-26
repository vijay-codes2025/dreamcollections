package com.dreamcollections.services.cart.client;

import com.dreamcollections.services.cart.dto.ProductVariantDetailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional; // Optional may not be well-supported by all Feign decoders for 404s.
                           // Often, it's better to return the DTO directly and handle potential 404s
                           // via FeignException or a custom ErrorDecoder. For now, let's try with DTO.

@FeignClient(name = "product-catalog-service", url = "${services.product-catalog.url}") // Name for service discovery, URL for direct connection
public interface ProductCatalogServiceClient {

    // Path matches the ProductController in product-catalog-service
    @GetMapping("/products/variants/{variantId}")
    ProductVariantDetailDto getProductVariantById(@PathVariable("variantId") Long variantId);
    // If getProductVariantById can return 404, Feign will throw FeignException.
    // We can catch this or use a custom ErrorDecoder.

    // Endpoint to get multiple variants by ID - assuming ProductCatalogService has this
    // The path in ProductController was /products/variants/findByIds
    @PostMapping("/products/variants/findByIds")
    List<ProductVariantDetailDto> getProductVariantsByIds(@RequestBody List<Long> variantIds);

    // We might also need an endpoint to get a single Product's details if a variant needs more parent product info
    // than what ProductVariantDetailDto provides, but typically ProductVariantDetailDto should be sufficient.
}
