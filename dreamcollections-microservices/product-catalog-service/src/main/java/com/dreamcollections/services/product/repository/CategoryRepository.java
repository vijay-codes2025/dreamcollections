package com.dreamcollections.services.product.repository;

import com.dreamcollections.services.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);

    // Method to find top-level categories (those with no parent)
    List<Category> findByParentCategoryIsNull();

    // Method to find sub-categories of a given parent category ID
    List<Category> findByParentCategoryId(Long parentCategoryId);
}
