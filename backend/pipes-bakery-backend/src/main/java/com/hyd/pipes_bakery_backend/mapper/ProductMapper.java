package com.hyd.pipes_bakery_backend.mapper;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;
import com.hyd.pipes_bakery_backend.model.Product;

@Component
public class ProductMapper {


    @NonNull
    public ProductResponseDTO toDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO(product.getId(), 
                                                        product.getName(), 
                                                        product.getDescription(), 
                                                        product.getPrice(),
                                                        product.getIngredients());
        return dto;
    }


    @NonNull
    public Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setIngredients(dto.getIngredients());
        return product;
    }
}
