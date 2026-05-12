package com.hyd.pipes_bakery_backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;
import com.hyd.pipes_bakery_backend.exception.InvalidProductOrderException;
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
        return productRepository.findAllByOrderByDisplayOrderAscIdAsc()
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
        product.setDisplayOrder(getNextDisplayOrder());
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

    @Override
    public List<ProductResponseDTO> updateProductOrder(List<Long> productIds) {
        List<Product> products = productRepository.findAll();

        validateProductOrder(productIds, products);

        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (int index = 0; index < productIds.size(); index++) {
            productsById.get(productIds.get(index)).setDisplayOrder(index);
        }

        return productRepository.saveAll(new ArrayList<>(productsById.values()))
                .stream()
                .sorted((firstProduct, secondProduct) -> Integer.compare(firstProduct.getDisplayOrder(), secondProduct.getDisplayOrder()))
                .map(productMapper::toDto)
                .toList();
    }

    private int getNextDisplayOrder() {
        return productRepository.findTopByOrderByDisplayOrderDesc()
                .map(product -> product.getDisplayOrder() + 1)
                .orElse(0);
    }

    private void validateProductOrder(List<Long> productIds, List<Product> products) {
        if (productIds.size() != products.size()) {
            throw new InvalidProductOrderException("Product order must include every product exactly once.");
        }

        if (new HashSet<>(productIds).size() != productIds.size()) {
            throw new InvalidProductOrderException("Product order cannot contain duplicated product IDs.");
        }

        HashSet<Long> existingProductIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toCollection(HashSet::new));

        if (!existingProductIds.containsAll(productIds)) {
            throw new InvalidProductOrderException("Product order contains unknown product IDs.");
        }
    }
}
