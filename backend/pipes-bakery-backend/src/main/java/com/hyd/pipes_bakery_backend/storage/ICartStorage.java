package com.hyd.pipes_bakery_backend.storage;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.model.ShoppingCart;

public interface ICartStorage {

    ShoppingCart getCart(Long clientId);

    void saveCart(Long clientId, @NonNull ShoppingCart cart);

    void clearCart(Long clientId);
}
