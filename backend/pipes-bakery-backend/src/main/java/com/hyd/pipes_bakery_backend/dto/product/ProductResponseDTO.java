package com.hyd.pipes_bakery_backend.dto.product;

import java.math.BigDecimal;
import java.util.List;

public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private List<String> ingredients;
    private String imageUrl;

    public ProductResponseDTO(Long id, String name, String description, BigDecimal price, List<String> ingredients, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }   
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setId(Long id) {
        this.id = id;
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
