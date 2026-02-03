package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;

public interface IProductService {

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductById(@NonNull Long id);

    ProductResponseDTO createProduct(ProductRequestDTO product);

    void deleteProduct(@NonNull Long id);

    ProductResponseDTO updateProduct(@NonNull Long id, ProductRequestDTO updatedProduct);
}