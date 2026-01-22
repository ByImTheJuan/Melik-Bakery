package com.hyd.pipes_bakery_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.service.ShoppingCartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shopping-carts")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    // GET /api/shopping-carts
    @GetMapping
    public List<ShoppingCartResponseDTO> getAllShoppingCarts() {
        return shoppingCartService.getAllShoppingCarts();
    }

    // GET /api/shopping-carts/{id}
    @GetMapping("/{id}")
    public ShoppingCartResponseDTO getShoppingCartById(@PathVariable Long id) {
        return shoppingCartService.getShoppingCartById(id);
    }

    // POST /api/shopping-carts
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartResponseDTO createShoppingCart(@Valid @RequestBody ShoppingCartRequestDTO shoppingCart) {
        return shoppingCartService.createShoppingCart(shoppingCart);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ShoppingCartResponseDTO updateShoppingCart(@PathVariable Long id, @Valid@RequestBody ShoppingCartRequestDTO updatedShoppingCart) {
        return shoppingCartService.updateShoppingCart(id, updatedShoppingCart);
    }

    // DELETE /api/shopping-carts/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShoppingCart(@PathVariable Long id) {
        shoppingCartService.deleteShoppingCart(id);
    }
}
