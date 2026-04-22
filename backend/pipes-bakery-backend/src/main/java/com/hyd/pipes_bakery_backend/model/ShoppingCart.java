package com.hyd.pipes_bakery_backend.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;


public class ShoppingCart {

    private UUID cartId;
    private List<CartItem> items;
    private final BigDecimal shippingCost = new BigDecimal("10000");
    private BigDecimal itemsTotal;
    private BigDecimal totalPrice;


    public ShoppingCart() {}

    public ShoppingCart(UUID cartId) {
        this.cartId = cartId;
        this.items = new ArrayList<>();
        recalculateTotals();
    }


    public UUID getCartId() {
        return cartId;
    }

    public List<CartItem> getItems() {
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
        recalculateTotals();
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
        if (itemToRemove != null) {
            items.remove(itemToRemove);
        } else {
            throw new ResourceNotFoundException("CartItem with productId " + productId + " not found in cart");
        }
        recalculateTotals();
    }

    public void updateItemQuantity(long productId, int quantity) {
        CartItem item = getItemByProductId(productId);
        if (item != null) {
            item.setQuantity(quantity);
        }
        recalculateTotals();
    }

    public void clearCart() {
        items.clear();
        recalculateTotals();
    }

    public void setcartId(UUID cartId) {
        this.cartId = cartId;
    }
    public void setItems(List<CartItem> items) {
        this.items = items;
        recalculateTotals();
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public BigDecimal getItemsTotal() {
        return itemsTotal;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    private void recalculateTotals() {
        this.itemsTotal = calculateItemsTotal();
        this.totalPrice = calculateTotalPrice();
    }

    private BigDecimal calculateItemsTotal() {
        BigDecimal total = BigDecimal.ZERO;

        if (items == null || items.isEmpty()) {
            return total;
        }

        for (CartItem item : items) {
            if (item != null) {
                total = total.add(item.getTotalPrice());
            }
        }

        return total;
    }

    private BigDecimal calculateTotalPrice() {
        return getItemsTotal().add(getShippingCost());
    }
}

