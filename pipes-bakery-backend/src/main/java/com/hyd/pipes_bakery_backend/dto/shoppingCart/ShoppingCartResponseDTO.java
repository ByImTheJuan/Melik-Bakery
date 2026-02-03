package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import java.util.Set;

import com.hyd.pipes_bakery_backend.model.CartItem;

public class ShoppingCartResponseDTO {

    private long clientId;
    private Set<CartItem> items;

    public ShoppingCartResponseDTO(Long clientId, Set<CartItem> items) {

        this.clientId = clientId;
        this.items = items;
    }

    public Long getClientId() {
        return clientId;
    }
    public Set<CartItem> getItems() {
        return items;
    }
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    public void setItems(Set<CartItem> items) {
        this.items = items;
    }
}
