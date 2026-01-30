package com.hyd.pipes_bakery_backend.service;

import org.springframework.lang.NonNull ;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.AddCartItemRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;

public interface IShoppingCartService {

    ShoppingCartResponseDTO getCartByClientId(Long clientId);

    ShoppingCartResponseDTO addItem(Long clientId, @NonNull AddCartItemRequestDTO dto);

    ShoppingCartResponseDTO updateItemQuantity(Long clientId, Long productId, int quantity);

    void removeItem(Long clientId, Long productId);

    void clearCart(Long clientId);

    double calculateTotal(Long clientId);
}