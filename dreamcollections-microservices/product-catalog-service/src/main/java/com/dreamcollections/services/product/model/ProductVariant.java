package com.dreamcollections.services.product.model; // Updated package

import jakarta.persistence.*;

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
    public ProductVariant() {
    }

    public ProductVariant(Product product, String size, Integer stockQuantity) {
        this.product = product;
        this.size = size;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return "ProductVariant{" +
               "id=" + id +
               ", productId=" + (product != null ? product.getId() : "null") +
               ", size='" + size + '\'' +
               ", stockQuantity=" + stockQuantity +
               '}';
    }
}
