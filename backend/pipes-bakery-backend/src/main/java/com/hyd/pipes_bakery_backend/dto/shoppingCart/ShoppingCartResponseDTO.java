package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import java.util.Set;
import java.util.UUID;

import com.hyd.pipes_bakery_backend.model.CartItem;

public class ShoppingCartResponseDTO {

    private UUID cartId;
    private Set<CartItem> items;

    public ShoppingCartResponseDTO(UUID cartId, Set<CartItem> items) {

        this.cartId = cartId;
        this.items = items;
    }

    public UUID getcartId() {
        return cartId;
    }
    public Set<CartItem> getItems() {
        return items;
    }
    public void setcartId(UUID cartId) {
        this.cartId = cartId;
    }
    public void setItems(Set<CartItem> items) {
        this.items = items;
    }
}
