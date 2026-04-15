package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.ProductMapper;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductResponseDTO getProductById(@NonNull Long id) {
        return productRepository.findById(id).map(productMapper::toDto).orElseThrow(() -> new ResourceNotFoundException(
                "Product not found with id " + id
        ));
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Product product = productMapper.toEntity(dto);
        System.out.println(product.getIngredients());
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    public void deleteProduct(@NonNull Long id) {
        if(productRepository.existsById(id))
            productRepository.deleteById(id);

        else
            throw new ResourceNotFoundException("Product not found with id " + id);
    }

    @Override
    public ProductResponseDTO updateProduct(@NonNull Long id, ProductRequestDTO updatedProduct) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setPrice(updatedProduct.getPrice());
                    product.setDescription(updatedProduct.getDescription());
                    product.setIngredients(updatedProduct.getIngredients());
                    product.setImageUrl(updatedProduct.getImageUrl());
                    return productRepository.save(product);
                })
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }
}