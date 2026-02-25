package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import java.util.Set;

import com.hyd.pipes_bakery_backend.model.CartItem;

public class ShoppingCartResponseDTO {

    private Long cartId;
    private Set<CartItem> items;

    public ShoppingCartResponseDTO(Long cartId, Set<CartItem> items) {

        this.cartId = cartId;
        this.items = items;
    }

    public Long getcartId() {
        return cartId;
    }
    public Set<CartItem> getItems() {
        return items;
    }
    public void setcartId(Long cartId) {
        this.cartId = cartId;
    }
    public void setItems(Set<CartItem> items) {
        this.items = items;
    }
}
