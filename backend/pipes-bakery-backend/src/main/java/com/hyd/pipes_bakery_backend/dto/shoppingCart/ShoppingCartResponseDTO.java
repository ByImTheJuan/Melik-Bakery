package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import java.util.List;
import java.util.UUID;

import com.hyd.pipes_bakery_backend.model.CartItem;

public class ShoppingCartResponseDTO {

    private UUID cartId;
    private List<CartItem> items;

    public ShoppingCartResponseDTO(UUID cartId, List<CartItem> items) {

        this.cartId = cartId;
        this.items = items;
    }

    public UUID getcartId() {
        return cartId;
    }
    public List<CartItem> getItems() {
        return items;
    }
    public void setcartId(UUID cartId) {
        this.cartId = cartId;
    }
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
