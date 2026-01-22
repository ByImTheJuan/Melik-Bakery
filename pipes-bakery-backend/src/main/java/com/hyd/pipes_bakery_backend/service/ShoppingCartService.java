package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.ShoppingCartRepository;

@Service
public class ShoppingCartService implements IShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @Override
    public List<ShoppingCartResponseDTO> getAllShoppingCarts() {
        return shoppingCartRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ShoppingCartResponseDTO getShoppingCartById(Long id) {
        return shoppingCartRepository.findById(id).map(this::toDto).orElseThrow(() -> new ResourceNotFoundException(
                "Shopping Cart not found with id " + id
        ));
    }

    @Override
    public ShoppingCartResponseDTO createShoppingCart(ShoppingCartRequestDTO dto) {
        ShoppingCart shoppingCart;
        shoppingCart = toEntity(dto);
        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);
        return toDto(savedShoppingCart);
    }

    @Override
    public void deleteShoppingCart(Long id) {
        if(shoppingCartRepository.existsById(id))
            shoppingCartRepository.deleteById(id);

        else
            throw new ResourceNotFoundException("Shopping Cart not found with id " + id);
    }

    @Override
    public ShoppingCartResponseDTO updateShoppingCart(Long id, ShoppingCartRequestDTO updatedShoppingCart) {
        return shoppingCartRepository.findById(id)
                .map(shoppingCart -> {
                    shoppingCart.setClient(updatedShoppingCart.getClient());
                    shoppingCart.setItems(updatedShoppingCart.getItems());
                    return shoppingCartRepository.save(shoppingCart);
                })
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping Cart not found with id " + id));
    }

    @Override
    public ShoppingCartResponseDTO toDto(ShoppingCart shoppingCart) {
        ShoppingCartResponseDTO dto = new ShoppingCartResponseDTO(shoppingCart.getId(), 
                                                        shoppingCart.getClient(), 
                                                        shoppingCart.getItems());
        return dto;
    }

    @Override
    public ShoppingCart toEntity(ShoppingCartRequestDTO dto) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setClient(dto.getClient());
        shoppingCart.setItems(dto.getItems());
        return shoppingCart;
    }
}
