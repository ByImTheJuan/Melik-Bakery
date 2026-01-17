package com.hyd.pipes_bakery_backend.dto.product;

public class ProductRequestDTO {
    private String name;
    private String description;
    private double price;
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
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
