package com.hyd.pipes_bakery_backend.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.AddCartItemRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@Service
public class ShoppingCartService implements IShoppingCartService {

    private final CartStorage cartStorage;
    private final ProductRepository productRepository;

    public ShoppingCartService(CartStorage cartStorage,
                               ProductRepository productRepository) {
        this.cartStorage = cartStorage;
        this.productRepository = productRepository;
    }

    @Override
    public ShoppingCartResponseDTO getCartByClientId(Long clientId) {
        ShoppingCart cart = cartStorage.getCart(clientId);
        return toDto(cart);
    }

    @Override
    public ShoppingCartResponseDTO addItem(Long clientId, @NonNull AddCartItemRequestDTO dto) {

        ShoppingCart cart = cartStorage.getCart(clientId);

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        OrderItem item = new OrderItem(product, dto.getQuantity()); // PRECIO CONGELADO AQUÍ

        cart.addItem(item);

        cartStorage.saveCart(clientId, cart);

        return toDto(cart);
    }

    @Override
    public ShoppingCartResponseDTO updateItemQuantity(Long clientId, Long productId, int quantity) {
        ShoppingCart cart = cartStorage.getCart(clientId);
        cart.updateItemQuantity(productId, quantity);
        cartStorage.saveCart(clientId, cart);
        return toDto(cart);
    }

    @Override
    public void removeItem(Long clientId, Long productId) {
        ShoppingCart cart = cartStorage.getCart(clientId);
        cart.removeItem(productId);
        cartStorage.saveCart(clientId, cart);
    }

    @Override
    public void clearCart(Long clientId) {
        cartStorage.clearCart(clientId);
    }

    @Override
    public double calculateTotal(Long clientId) {
        ShoppingCart cart = cartStorage.getCart(clientId);
        return cart.getTotalPrice();
    }

    // mapper privado
    private ShoppingCartResponseDTO toDto(ShoppingCart cart) {
        ShoppingCartResponseDTO dto = new ShoppingCartResponseDTO(cart.getClientId(), cart.getItems());
        return dto;
    }
}