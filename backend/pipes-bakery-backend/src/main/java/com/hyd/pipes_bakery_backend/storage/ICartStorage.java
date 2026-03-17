package com.hyd.pipes_bakery_backend.storage;

import java.util.UUID;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.model.ShoppingCart;

public interface ICartStorage {

    ShoppingCart getCart(UUID clientId);

    ShoppingCart createCart();

    void saveCart(UUID clientId, @NonNull ShoppingCart cart);

    void clearCart(UUID clientId);
}
