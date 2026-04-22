package com.hyd.pipes_bakery_backend.mapper;

import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;

@Component
public class ShoppingCartMapper {


    public ShoppingCartResponseDTO toDto(ShoppingCart cart) {
        ShoppingCartResponseDTO dto = new ShoppingCartResponseDTO(cart.getCartId(),
            cart.getItems(), cart.getShippingCost(), cart.getItemsTotal(), cart.getTotalPrice());
        return dto;
    }
}
