package com.hyd.pipes_bakery_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.AddCartItemRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.service.OrderService;
import com.hyd.pipes_bakery_backend.service.ShoppingCartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService cartService;
    private final OrderService orderService;

    public ShoppingCartController(ShoppingCartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    //GET /api/cart/{clientId}
    @GetMapping("/{clientId}")
    public ShoppingCartResponseDTO getCart(@PathVariable Long clientId) {
        return cartService.getCartByClientId(clientId);
    }

    //POST /api/cart/{clientId}/items
    @PostMapping("/{clientId}/items")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartResponseDTO addItem(
            @PathVariable Long clientId,
            @NonNull @RequestBody @Valid AddCartItemRequestDTO request) {

        return cartService.addItem(clientId, request);
    }

    //CHECKOUT /api/cart/{clientId}/checkout
    @PostMapping("/{clientId}/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO checkout(
            @PathVariable Long clientId,
            @RequestBody @Valid CheckoutOrderRequestDTO dto)   {

        return orderService.checkout(clientId, dto);
    }

    //DELETE /api/cart/{clientId}/items/{productId}
    @DeleteMapping("/{clientId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            @PathVariable Long clientId,
            @PathVariable Long productId) {

        cartService.removeItem(clientId, productId);
    }

    //DELETE /api/cart/{clientId}
    @DeleteMapping("/{clientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable Long clientId) {
        cartService.clearCart(clientId);
    }
}