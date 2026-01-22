package com.hyd.pipes_bakery_backend.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "shoppingCart")
    private Client client;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
    private Set<OrderItem> items;

    public ShoppingCart() {
        
    }

    public ShoppingCart(Client client) {
        this.client = client;
        this.items = new HashSet<>();
    }


    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    public double getTotalPrice() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    public void clearCart() {
        items.clear();
    }

    public void setClient(Client client) {
        this.client = client;
    }
    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }
}
