package com.hyd.pipes_bakery_backend.mapper;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartMapper {


    public ShoppingCartResponseDTO toDto(ShoppingCart cart) {
        ShoppingCartResponseDTO dto = new ShoppingCartResponseDTO(cart.getCartId(), cart.getItems());
        return dto;
    }
}
