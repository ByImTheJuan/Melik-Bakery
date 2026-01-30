package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import java.util.Set;

import com.hyd.pipes_bakery_backend.model.OrderItem;

public class ShoppingCartResponseDTO {

    private long clientId;
    private Set<OrderItem> items;

    public ShoppingCartResponseDTO(Long clientId, Set<OrderItem> items) {

        this.clientId = clientId;
        this.items = items;
    }

    public Long getClientId() {
        return clientId;
    }
    public Set<OrderItem> getItems() {
        return items;
    }
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }
}
