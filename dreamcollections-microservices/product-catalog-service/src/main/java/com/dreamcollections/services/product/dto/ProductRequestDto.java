package com.dreamcollections.services.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public class ProductRequestDto {

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String name;

    @Size(max = 2000, message = "Description can be up to 2000 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @Size(max = 512, message = "Image URL can be up to 512 characters")
    private String imageUrl;

    @Size(max = 512, message = "Video URL can be up to 512 characters")
    private String videoUrl;

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;

    @Valid
    @NotNull(message = "Product must have at least one variant")
    @Size(min = 1, message = "Product must have at least one variant")
    private List<ProductVariantDto> variants;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public List<ProductVariantDto> getVariants() { return variants; }
    public void setVariants(List<ProductVariantDto> variants) { this.variants = variants; }
}
