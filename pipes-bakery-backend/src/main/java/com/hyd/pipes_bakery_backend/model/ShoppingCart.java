package com.hyd.pipes_bakery_backend.model;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private final Client client;
    private final Map<Product, Integer> products;

    public ShoppingCart(Client client) {
        this.client = client;
        this.products = new HashMap<>();
    }

    public Client getClient() {
        return client;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        if(products.get(product) != null) {
            int quantity = products.get(product);
            products.put(product, quantity + 1);
        }
        else {
            products.put(product, 1);
        }
    }

    public void removeProduct(Product product) {
        if(products.get(product) > 1)
            products.put(product, products.get(product) - 1);
        else {
            products.remove(product);
        }
            
    }

    public double getTotalPrice() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }
}
