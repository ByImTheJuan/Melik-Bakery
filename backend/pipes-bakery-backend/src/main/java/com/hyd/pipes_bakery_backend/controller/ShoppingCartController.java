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

    //GET /api/cart/{cartId}
    @GetMapping("/{cartId}")
    public ShoppingCartResponseDTO getCart(@PathVariable Long cartId) {
        return cartService.getCartById(cartId);
    }

    //POST /api/cart/{cartId}/items
    @PostMapping("/{cartId}/items")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartResponseDTO addItem(
            @PathVariable Long cartId,
            @NonNull @RequestBody @Valid AddCartItemRequestDTO request) {

        return cartService.addItem(cartId, request);
    }

    //CHECKOUT /api/cart/{cartId}/checkout
    @PostMapping("/{cartId}/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO checkout(
            @PathVariable Long cartId,
            @RequestBody @Valid CheckoutOrderRequestDTO dto)   {

        return orderService.checkout(cartId, dto);
    }

    //DELETE /api/cart/{cartId}/items/{productId}
    @DeleteMapping("/{cartId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            @PathVariable Long cartId,
            @PathVariable Long productId) {

        cartService.removeItem(cartId, productId);
    }

    //DELETE /api/cart/{cartId}
    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
    }
}