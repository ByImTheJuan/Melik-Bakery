package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import java.util.Set;

import com.hyd.pipes_bakery_backend.model.Client;
import com.hyd.pipes_bakery_backend.model.OrderItem;

public class ShoppingCartRequestDTO {

    private Client client;
    private Set<OrderItem> items;

    public Client getClient() {
        return client;
    }
    public Set<OrderItem> getItems() {
        return items;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }
}
