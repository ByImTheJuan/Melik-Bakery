package com.hyd.pipes_bakery_backend.model;

public class Product {
    private final String name;
    private final double price;
    private final String description;
    private final String ingredients;

    public Product(String name, double price, String description, String ingredients) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public double getPrice() {
        return price;
    }
}
