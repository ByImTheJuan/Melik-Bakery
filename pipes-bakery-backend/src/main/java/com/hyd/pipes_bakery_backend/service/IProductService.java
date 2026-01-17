package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;
import com.hyd.pipes_bakery_backend.model.Product;

public interface IProductService {

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO createProduct(ProductRequestDTO product);

    void deleteProduct(Long id);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProduct);

    ProductResponseDTO toDto(Product product);

    Product toEntity(ProductRequestDTO dto);
}