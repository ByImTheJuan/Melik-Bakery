package com.hyd.pipes_bakery_backend.model;

import java.util.HashSet;
import java.util.Set;


public class ShoppingCart {

    private Long clientId;
    private Set<OrderItem> items;

    public ShoppingCart() {
        
    }

    public ShoppingCart(long clientId) {
        this.clientId = clientId;
        this.items = new HashSet<>();
    }


    public Long getClientId() {
        return clientId;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void addItem(OrderItem newItem) {
        OrderItem existingItem = getItemByProductId(newItem.getProduct().getId());
        if (existingItem != null) {
            existingItem.increaseQuantity(newItem.getQuantity());
        } else {
            items.add(newItem);
        }
    }

    public OrderItem getItemByProductId(long productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getId() == productId) {
                return item;
            }
        }
        return null;
    }

    public void removeItem(long productId) {
        OrderItem itemToRemove = getItemByProductId(productId);
        if (itemToRemove.getQuantity() == 1) {
            items.remove(itemToRemove);
        } else {
            itemToRemove.setQuantity(itemToRemove.getQuantity() - 1);
        }
    }

    public void updateItemQuantity(long productId, int quantity) {
        OrderItem item = getItemByProductId(productId);
        if (item != null) {
            item.setQuantity(quantity);
        }
    }

    public double getTotalPrice() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clearCart() {
        items.clear();
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }
}
