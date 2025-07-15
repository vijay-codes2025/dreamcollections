package com.dreamcollections.services.product.service.impl;

import com.dreamcollections.services.product.dto.CategoryDto;
import com.dreamcollections.services.product.dto.ProductRequestDto;
import com.dreamcollections.services.product.dto.ProductResponseDto;
import com.dreamcollections.services.product.dto.ProductVariantDto;
import com.dreamcollections.services.product.exception.BadRequestException; // Will create this
import com.dreamcollections.services.product.exception.ResourceNotFoundException;
import com.dreamcollections.services.product.model.Category;
import com.dreamcollections.services.product.model.Product;
import com.dreamcollections.services.product.model.ProductVariant;
import com.dreamcollections.services.product.repository.CategoryRepository;
import com.dreamcollections.services.product.repository.ProductRepository;
import com.dreamcollections.services.product.repository.ProductVariantRepository;
import com.dreamcollections.services.product.service.CategoryService;
import com.dreamcollections.services.product.service.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private CategoryService categoryService;

    // --- Mapper Methods ---
    private ProductVariantDto mapVariantToDto(ProductVariant variant) {
        if (variant == null) return null;
        return new ProductVariantDto(
                variant.getId(),
                variant.getSize(),
                variant.getStockQuantity(),
                variant.getProduct() != null ? variant.getProduct().getId() : null);
    }

    private ProductVariant mapDtoToVariant(ProductVariantDto dto, Product product) {
        if (dto == null) return null;
        ProductVariant variant = new ProductVariant();
        if (dto.getId() != null) { // For updates, ID might be present
            variant.setId(dto.getId());
        }
        variant.setProduct(product);
        variant.setSize(dto.getSize());
        variant.setStockQuantity(dto.getStockQuantity());
        return variant;
    }

    private ProductResponseDto mapProductToResponseDto(Product product) {
        if (product == null) return null;

        Category category = product.getCategory();
        CategoryDto categoryDto = null;
        if (category != null) {
            categoryDto = new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription());
        }

        List<ProductVariantDto> variantDtos = Collections.emptyList();
        if (!CollectionUtils.isEmpty(product.getVariants())) {
             variantDtos = product.getVariants().stream()
                .map(this::mapVariantToDto)
                .collect(Collectors.toList());
        }


        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getVideoUrl(),
                categoryDto,
                variantDtos);
    }


    // --- Service Methods ---
    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        log.info("Creating product: {}", productRequestDto.getName());
        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequestDto.getCategoryId()));

        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setImageUrl(productRequestDto.getImageUrl());
        product.setVideoUrl(productRequestDto.getVideoUrl());
        product.setCategory(category);

        // Add variants
        if (productRequestDto.getVariants() != null && !productRequestDto.getVariants().isEmpty()) {
            productRequestDto.getVariants().forEach(variantDto -> {
                product.addVariant(mapDtoToVariant(variantDto, product));
            });
        } else {
            log.warn("Product {} is being created without variants.", productRequestDto.getName());
            // Depending on business rules, this might be an error or allowed.
            // throw new BadRequestException("Product must have at least one variant.");
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product {} created successfully with ID {}.", savedProduct.getName(), savedProduct.getId());
        return mapProductToResponseDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponseDto> getProductById(Long id) {
        log.debug("Fetching product by ID: {}", id);
        // Use findByIdWithDetails to ensure variants and category are fetched
        return productRepository.findByIdWithDetails(id).map(this::mapProductToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        log.debug("Fetching all products, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findAll(pageable).map(this::mapProductToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsByCategoryId(Long categoryId, Pageable pageable) {
        log.debug("Fetching products for category ID: {}, page: {}, size: {}", categoryId, pageable.getPageNumber(), pageable.getPageSize());
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        return productRepository.findByCategoryId(categoryId, pageable).map(this::mapProductToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsByCategoryIdIncludingSubcategories(Long categoryId, Pageable pageable) {
        log.debug("Fetching products for category ID including subcategories: {}, page: {}, size: {}", categoryId, pageable.getPageNumber(), pageable.getPageSize());
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }

        // Get all category IDs including subcategories
        List<Long> categoryIds = categoryService.getAllCategoryIdsIncludingSubcategories(categoryId);
        log.debug("Found category IDs for hierarchical filtering: {}", categoryIds);

        return productRepository.findByCategoryIdIn(categoryIds, pageable).map(this::mapProductToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> searchProductsByName(String name, Pageable pageable) {
        log.debug("Searching products by name: '{}', page: {}, size: {}", name, pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findByNameContainingIgnoreCase(name, pageable).map(this::mapProductToResponseDto);
    }


    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
        log.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequestDto.getCategoryId()));

        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setImageUrl(productRequestDto.getImageUrl());
        product.setVideoUrl(productRequestDto.getVideoUrl());
        product.setCategory(category);

        // Variant management: clear existing and add new ones from DTO
        // This approach relies on orphanRemoval=true for Product.variants
        product.getVariants().clear();
        // productVariantRepository.deleteAll(product.getVariants()); // explicit delete, then flush
        // productVariantRepository.flush(); // ensure deletes happen before adds if IDs could overlap (not the case here as we're creating new)

        if (productRequestDto.getVariants() != null && !productRequestDto.getVariants().isEmpty()) {
            productRequestDto.getVariants().forEach(variantDto -> {
                ProductVariant newVariant = mapDtoToVariant(variantDto, product);
                // variantDto.getId() is ignored here, always creating new variant instances for the set.
                // If you need to update existing variants by ID, the logic would be more complex:
                // fetch existing, update, remove ones not in DTO, add new ones from DTO.
                product.addVariant(newVariant);
            });
        } else {
             log.warn("Product {} is being updated to have no variants.", product.getName());
            // throw new BadRequestException("Product must have at least one variant.");
        }


        Product updatedProduct = productRepository.save(product);
        log.info("Product {} updated successfully.", updatedProduct.getName());
        return mapProductToResponseDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id); // Cascades to variants due to orphanRemoval=true
        log.info("Product with ID: {} deleted successfully.", id);
    }

    @Override
    @Transactional
    public ProductVariantDto updateStock(Long productVariantId, Integer newStockQuantity) {
        log.info("Updating stock for variant ID: {} to quantity: {}", productVariantId, newStockQuantity);
        if (newStockQuantity < 0) {
            throw new BadRequestException("Stock quantity cannot be negative.");
        }
        ProductVariant variant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", productVariantId));

        variant.setStockQuantity(newStockQuantity);
        ProductVariant savedVariant = productVariantRepository.save(variant);
        log.info("Stock updated for variant ID: {}. New stock: {}", productVariantId, savedVariant.getStockQuantity());
        return mapVariantToDto(savedVariant);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariantDto> getProductVariantById(Long productVariantId) {
        log.debug("Fetching product variant by ID: {}", productVariantId);
        return productVariantRepository.findById(productVariantId).map(this::mapVariantToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDto> getProductVariantsByIds(List<Long> variantIds) {
        log.debug("Fetching product variants by IDs: {}", variantIds);
        if (CollectionUtils.isEmpty(variantIds)) {
            return Collections.emptyList();
        }
        return productVariantRepository.findByIdIn(variantIds).stream()
                                     .map(this::mapVariantToDto)
                                     .collect(Collectors.toList());
    }
}
