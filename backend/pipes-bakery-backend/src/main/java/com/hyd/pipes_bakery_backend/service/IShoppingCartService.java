package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.lang.NonNull ;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.AddCartItemRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.UpdateCartItemQuantityRequestDTO;

public interface IShoppingCartService {

    ShoppingCartResponseDTO getCartById(UUID cartId);

    ShoppingCartResponseDTO createCart();

    ShoppingCartResponseDTO addItem(UUID cartId, @NonNull AddCartItemRequestDTO dto);

    ShoppingCartResponseDTO updateItemQuantity(UUID cartId, Long productId, UpdateCartItemQuantityRequestDTO quantity);

    void removeItem(UUID cartId, Long productId);

    void clearCart(UUID cartId);

    BigDecimal calculateTotal(UUID cartId);
}