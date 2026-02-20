package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;

import org.springframework.lang.NonNull ;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.AddCartItemRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;

public interface IShoppingCartService {

    ShoppingCartResponseDTO getCartById(Long cartId);

    ShoppingCartResponseDTO addItem(Long cartId, @NonNull AddCartItemRequestDTO dto);

    ShoppingCartResponseDTO updateItemQuantity(Long cartId, Long productId, int quantity);

    void removeItem(Long cartId, Long productId);

    void clearCart(Long cartId);

    BigDecimal calculateTotal(Long cartId);
}