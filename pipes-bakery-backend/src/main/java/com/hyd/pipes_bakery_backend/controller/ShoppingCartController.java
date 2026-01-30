package com.hyd.pipes_bakery_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.AddCartItemRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.service.ShoppingCartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService cartService;

    public ShoppingCartController(ShoppingCartService cartService) {
        this.cartService = cartService;
    }

    //GET /api/cart/{clientId}
    @GetMapping("/{clientId}")
    public ShoppingCartResponseDTO getCart(@PathVariable Long clientId) {
        return cartService.getCartByClientId(clientId);
    }

    //POST /api/cart/{clientId}/items
    @PostMapping("/{clientId}/items")
    public ResponseEntity<Void> addItem(
            @PathVariable Long clientId,
            @NonNull @RequestBody @Valid AddCartItemRequestDTO dto) {

        cartService.addItem(clientId, dto);
        return ResponseEntity.ok().build();
    }

    //DELETE /api/cart/{clientId}/items/{productId}
    @DeleteMapping("/{clientId}/items/{productId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long clientId,
            @PathVariable Long productId) {

        cartService.removeItem(clientId, productId);
        return ResponseEntity.noContent().build();
    }

    //DELETE /api/cart/{clientId}
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long clientId) {
        cartService.clearCart(clientId);
        return ResponseEntity.noContent().build();
    }
}