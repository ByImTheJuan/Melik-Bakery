package com.hyd.pipes_bakery_backend.storage;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;

@Component
public class CartStorage implements ICartStorage {

    private final RedisTemplate<String, ShoppingCart> redisTemplate;

    public CartStorage(RedisTemplate<String, ShoppingCart> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private @NonNull String key(UUID cartId) {
        return "cart:" + cartId;
    }

    @Override
    public ShoppingCart getCart(UUID cartId) {
        ShoppingCart cart = redisTemplate.opsForValue().get(key(cartId));

        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found with id " + cartId);
        }

        return cart;
    }

    @Override
    public ShoppingCart createCart() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());
        saveCart(cart.getCartId(), cart);
        return cart;
    }

    @Override
    public void saveCart(UUID cartId, @NonNull ShoppingCart cart) {
        redisTemplate.opsForValue().set(key(cartId), cart);
    }

    @Override
    public void clearCart(UUID cartId) {
        redisTemplate.delete(key(cartId));
    }
}