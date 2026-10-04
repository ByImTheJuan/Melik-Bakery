package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.customcake.AddCustomCakeRequestDTO;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.AddCartItemRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.UpdateCartItemQuantityRequestDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.ShoppingCartMapper;
import com.hyd.pipes_bakery_backend.model.CartItem;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@Service
public class ShoppingCartService implements IShoppingCartService {

    public static final String CUSTOM_CAKE_NAME = "Torta personalizada";

    private final CartStorage cartStorage;
    private final ProductRepository productRepository;
    private final ICustomCakeService customCakeService;
    private final ShoppingCartMapper shoppingCartMapper = new ShoppingCartMapper();

    public ShoppingCartService(CartStorage cartStorage,
                               ProductRepository productRepository,
                               ICustomCakeService customCakeService) {
        this.cartStorage = cartStorage;
        this.productRepository = productRepository;
        this.customCakeService = customCakeService;
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

        CartItem item = new CartItem(product.getId(), product.getName(), dto.getQuantity(), product.getPrice(), product.getImageFile()); // PRECIO CONGELADO AQUÍ

        cart.addItem(item);

        cartStorage.saveCart(cartId, cart);

        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDTO updateItemQuantity(UUID cartId, Long productId, UpdateCartItemQuantityRequestDTO dto) {
        ShoppingCart cart = cartStorage.getCart(cartId);
        cart.updateItemQuantity(productId, dto.getQuantity());
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
    public ShoppingCartResponseDTO addCustomCake(UUID cartId, @NonNull AddCustomCakeRequestDTO dto) {
        ShoppingCart cart = cartStorage.getCart(cartId);

        // The price is always computed here from the catalog, never taken from the client
        CustomCakeDetails details = customCakeService.resolve(dto.getConfiguration());
        CartItem item = CartItem.customCake(
                UUID.randomUUID().toString(),
                CUSTOM_CAKE_NAME,
                dto.getQuantity(),
                customCakeService.price(details),
                details
        );

        cart.addItem(item);
        cartStorage.saveCart(cartId, cart);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDTO updateCustomCakeQuantity(UUID cartId, String lineId, UpdateCartItemQuantityRequestDTO dto) {
        ShoppingCart cart = cartStorage.getCart(cartId);
        cart.updateCustomCakeQuantity(lineId, dto.getQuantity());
        cartStorage.saveCart(cartId, cart);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDTO removeCustomCake(UUID cartId, String lineId) {
        ShoppingCart cart = cartStorage.getCart(cartId);
        cart.removeCustomCake(lineId);
        cartStorage.saveCart(cartId, cart);
        return shoppingCartMapper.toDto(cart);
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