package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Product product = toEntity(dto);
        Product savedProduct = productRepository.save(product);
        return toDto(savedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if(productRepository.existsById(id))
            productRepository.deleteById(id);

        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProduct) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setPrice(updatedProduct.getPrice());
                    product.setDescription(updatedProduct.getDescription());
                    return productRepository.save(product);
                })
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public ProductResponseDTO toDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO(product.getId(), 
                                                        product.getName(), 
                                                        product.getDescription(), 
                                                        product.getPrice());
        return dto;
    }

    @Override
    public Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        return product;
    }
}