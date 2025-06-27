package com.dreamcollections.services.product.model; // Updated package

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Eager fetch might be useful if product is always needed with variant
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String size;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    // Constructors
    public ProductVariant(Product product, String size, Integer stockQuantity) {
        this.product = product;
        this.size = size;
        this.stockQuantity = stockQuantity;
    }
}
