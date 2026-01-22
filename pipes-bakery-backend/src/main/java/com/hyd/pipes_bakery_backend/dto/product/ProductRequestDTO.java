package com.hyd.pipes_bakery_backend.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductRequestDTO {
    private String name;
    private String description;
    private double price;
    
    @NotBlank(message = "Product name is required")
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
