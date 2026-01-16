package com.hyd.pipes_bakery_backend.model;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Client client;
    private Map<Product, Integer> products;

    public ShoppingCart() {
        
    }

    public ShoppingCart(Client client) {
        this.client = client;
        this.products = new HashMap<>();
    }


    public Long getId() {
        return id;
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

    public void clearCart() {
        products.clear();
    }
}
