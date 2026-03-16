package com.hyd.pipes_bakery_backend.dto.product;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ProductRequestDTO {
    
    @NotBlank(message = "Product name is required")
    private String name;
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;

    @NotNull
    @NotEmpty(message = "Ingredients list cannot be empty")
    private List<@NotBlank(message = "Elements in ingredients list cannot be blank") String> ingredients;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
