package com.hyd.pipes_bakery_backend.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ShoppingCart {

    private Long cartId;
    private Set<CartItem> items;


    public ShoppingCart() {}

    public ShoppingCart(Long cartId) {
        this.cartId = cartId;
        this.items = new HashSet<>();
    }


    public Long getCartId() {
        return cartId;
    }

    public Set<CartItem> getItems() {
        return items;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void addItem(CartItem newItem) {
        CartItem existingItem = getItemByProductId(newItem.getProductId());
        if (existingItem != null) {
            existingItem.increaseQuantity(newItem.getQuantity());
        } else {
            items.add(newItem);
        }
    }

    public CartItem getItemByProductId(long productId) {
        if (!items.isEmpty()) {
            for (CartItem item : items) {
                if (item.getProductId() == productId) {
                    return item;
                }
            }
        }
        return null;
    }

    public void removeItem(long productId) {
        CartItem itemToRemove = getItemByProductId(productId);
        if (itemToRemove.getQuantity() == 1) {
            items.remove(itemToRemove);
        } else {
            itemToRemove.setQuantity(itemToRemove.getQuantity() - 1);
        }
    }

    public void updateItemQuantity(long productId, int quantity) {
        CartItem item = getItemByProductId(productId);
        if (item != null) {
            item.setQuantity(quantity);
        }
    }

    @JsonIgnore
    public BigDecimal getTotalPrice() {
        BigDecimal total = new BigDecimal(0);
        for (CartItem item : items) {
            total = item.getTotalPrice().add(total);
        }
        return total;
    }

    public void clearCart() {
        items.clear();
    }

    public void setcartId(Long cartId) {
        this.cartId = cartId;
    }
    public void setItems(Set<CartItem> items) {
        this.items = items;
    }
}
