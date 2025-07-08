package com.dreamcollections.services.product.model; // Updated package

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class
Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    @JsonBackReference // To handle parent side of the relationship during JSON serialization
    @ToString.Exclude // Exclude from Lombok's toString to prevent recursion
    @EqualsAndHashCode.Exclude // Exclude from Lombok's equals/hashCode to prevent recursion
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // To handle children side of the relationship during JSON serialization
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Category> subCategories = new HashSet<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Product> products = new HashSet<>();

    // Constructors
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description, Category parentCategory) {
        this.name = name;
        this.description = description;
        this.parentCategory = parentCategory;
    }

    // Helper methods for managing subCategories
    public void addSubCategory(Category subCategory) {
        this.subCategories.add(subCategory);
        subCategory.setParentCategory(this);
    }

    public void removeSubCategory(Category subCategory) {
        this.subCategories.remove(subCategory);
        subCategory.setParentCategory(null);
    }
    // Lombok generates getters, setters
}
