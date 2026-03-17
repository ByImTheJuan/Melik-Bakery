package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.AddCartItemRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.ShoppingCartMapper;
import com.hyd.pipes_bakery_backend.model.CartItem;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@Service
public class ShoppingCartService implements IShoppingCartService {

    private final CartStorage cartStorage;
    private final ProductRepository productRepository;
    private final ShoppingCartMapper shoppingCartMapper = new ShoppingCartMapper();

    public ShoppingCartService(CartStorage cartStorage,
                               ProductRepository productRepository) {
        this.cartStorage = cartStorage;
        this.productRepository = productRepository;
    }

    @Override
    public ShoppingCartResponseDTO getCartById(UUID cartId) {
        ShoppingCart cart = cartStorage.getCart(cartId);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDTO createCart() {
        ShoppingCart cart = cartStorage.createCart();
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDTO addItem(UUID cartId, @NonNull AddCartItemRequestDTO dto) {

        ShoppingCart cart = cartStorage.getCart(cartId);

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem item = new CartItem(product.getId(), product.getName(), dto.getQuantity(), product.getPrice()); // PRECIO CONGELADO AQUÍ

        cart.addItem(item);

        cartStorage.saveCart(cartId, cart);

        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDTO updateItemQuantity(UUID cartId, Long productId, int quantity) {
        ShoppingCart cart = cartStorage.getCart(cartId);
        cart.updateItemQuantity(productId, quantity);
        cartStorage.saveCart(cartId, cart);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public void removeItem(UUID cartId, Long productId) {
        ShoppingCart cart = cartStorage.getCart(cartId);
        cart.removeItem(productId);
        cartStorage.saveCart(cartId, cart);
    }

    @Override
    public void clearCart(UUID cartId) {
        cartStorage.clearCart(cartId);
    }

    @Override
    public BigDecimal calculateTotal(UUID cartId) {
        ShoppingCart cart = cartStorage.getCart(cartId);
        return cart.getTotalPrice();
    }
}