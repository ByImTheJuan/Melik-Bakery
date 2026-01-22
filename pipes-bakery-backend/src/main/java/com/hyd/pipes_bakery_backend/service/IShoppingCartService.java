package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;

public interface IShoppingCartService {

    List<ShoppingCartResponseDTO> getAllShoppingCarts();

    ShoppingCartResponseDTO getShoppingCartById(Long id);

    ShoppingCartResponseDTO createShoppingCart(ShoppingCartRequestDTO shoppingCart);

    void deleteShoppingCart(Long id);

    ShoppingCartResponseDTO updateShoppingCart(Long id, ShoppingCartRequestDTO updatedShoppingCart);

    ShoppingCartResponseDTO toDto(ShoppingCart shoppingCart);

    ShoppingCart toEntity(ShoppingCartRequestDTO dto);
}