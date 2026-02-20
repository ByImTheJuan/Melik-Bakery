package com.hyd.pipes_bakery_backend.dto.product;

import java.math.BigDecimal;

public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String ingredients;

    public ProductResponseDTO(Long id, String name, String description, BigDecimal price, String ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.ingredients = ingredients;
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

    public String getIngredients() {
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

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
