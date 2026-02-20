package com.hyd.pipes_bakery_backend.storage;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.model.ShoppingCart;

@Component
public class CartStorage implements ICartStorage {

    private final RedisTemplate<String, ShoppingCart> redisTemplate;

    public CartStorage(RedisTemplate<String, ShoppingCart> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private @NonNull String key(Long clientId) {
        return "cart:" + clientId;
    }

    @Override
    public ShoppingCart getCart(Long clientId) {
        ShoppingCart cart = redisTemplate.opsForValue().get(key(clientId));

        if (cart == null) {
            cart = new ShoppingCart(clientId);
        }

        return cart;
    }

    @Override
    public void saveCart(Long clientId, @NonNull ShoppingCart cart) {
        redisTemplate.opsForValue().set(key(clientId), cart);
    }

    @Override
    public void clearCart(Long clientId) {
        redisTemplate.delete(key(clientId));
    }
}