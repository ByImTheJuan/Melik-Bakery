package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.ProductMapper;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    final private ProductMapper productMapper = new ProductMapper();

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, productMapper);
    }

    @SuppressWarnings("null")
    @Test
    void shouldCreateProductSuccessfully() {
        // Arrange
        ProductRequestDTO request = new ProductRequestDTO();

        request.setName("Baguette de masa madre");
        request.setPrice(new BigDecimal(5000));
        request.setDescription("Pan artesanal hecho con ingredientes naturales");
        request.setIngredients(Arrays.asList("Agua", "harina", "masa madre", "sal"));
        request.setImageUrl("https://example.com/images/baguette.jpg");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Baguette de masa madre");
        savedProduct.setPrice(new BigDecimal(5000));
        savedProduct.setDescription("Pan artesanal hecho con ingredientes naturales");
        savedProduct.setIngredients(Arrays.asList("Agua", "harina", "masa madre", "sal"));
        savedProduct.setImageUrl("https://example.com/images/baguette.jpg");

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        ProductResponseDTO result = productService.createProduct(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Baguette de masa madre");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal(5000));
        assertThat(result.getDescription()).isEqualTo("Pan artesanal hecho con ingredientes naturales");
        assertThat(result.getIngredients()).containsExactly("Agua", "harina", "masa madre", "sal");
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/images/baguette.jpg");

        verify(productRepository).save(any(Product.class));
    }


    @Test
    void shouldGetProductByIdSuccessfully() {
        
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Croissant");
        product.setPrice(new BigDecimal(3000));
        product.setDescription("Delicioso croissant francés");
        product.setIngredients(Arrays.asList("Harina", "mantequilla", "azúcar", "levadura", "sal"));
        product.setImageUrl("https://example.com/images/baguette.jpg");

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(product));

        // Act
        ProductResponseDTO result = productService.getProductById(productId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Croissant");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal(3000));
        assertThat(result.getDescription()).isEqualTo("Delicioso croissant francés");
        assertThat(result.getIngredients()).containsExactly("Harina", "mantequilla", "azúcar", "levadura", "sal");
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/images/baguette.jpg");

        verify(productRepository).findById(productId);
    }


    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        
        // Arrange
        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            productService.getProductById(productId);
        } catch (ResourceNotFoundException e) {
            assertThat(e).isInstanceOf(com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Product not found with id " + productId);
        }

        verify(productRepository).findById(productId);
    }


    @Test
    void shouldDeleteProductSuccessfully() {
        
        // Arrange
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        
        // Arrange
        Long productId = 999L;

        when(productRepository.existsById(productId)).thenReturn(false);

        // Act & Assert
        try {
            productService.deleteProduct(productId);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Product not found with id " + productId);
        }

        verify(productRepository).existsById(productId);
    }

    
    @SuppressWarnings("null")
    @Test
    void shouldUpdateProductSuccessfully() {
        
        // Arrange
        Long productId = 1L;
        ProductRequestDTO updatedRequest = new ProductRequestDTO();
        updatedRequest.setName("Pan de chocolate");
        updatedRequest.setPrice(new BigDecimal(4000));
        updatedRequest.setDescription("Delicioso pan relleno de chocolate");
        updatedRequest.setIngredients(Arrays.asList("Harina", "chocolate", "azúcar", "mantequilla", "levadura", "sal"));
        updatedRequest.setImageUrl("https://example.com/images/baguette.jpg");

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Pan simple");
        existingProduct.setPrice(new BigDecimal(2000));
        existingProduct.setDescription("Pan básico sin relleno");
        existingProduct.setIngredients(Arrays.asList("Harina", "agua", "sal", "levadura"));
        existingProduct.setImageUrl("https://example.com/images/cake.jpg");

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ProductResponseDTO result = productService.updateProduct(productId, updatedRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Pan de chocolate");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal(4000));
        assertThat(result.getDescription()).isEqualTo("Delicioso pan relleno de chocolate");
        assertThat(result.getIngredients()).containsExactly("Harina", "chocolate", "azúcar", "mantequilla", "levadura", "sal");
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/images/baguette.jpg");

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        
        // Arrange
        Long productId = 999L;
        ProductRequestDTO updatedRequest = new ProductRequestDTO();
        updatedRequest.setName("Pan inexistente");
        updatedRequest.setPrice(new BigDecimal(0));
        updatedRequest.setDescription("Este pan no existe");
        updatedRequest.setIngredients(Arrays.asList("N/A"));
        updatedRequest.setImageUrl("https://example.com/images/baguette.jpg");

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            productService.updateProduct(productId, updatedRequest);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Product not found with id " + productId);
        }

        verify(productRepository).findById(productId);
    }

    @Test
    void shouldGetAllProductsSuccessfully() {
        
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Pan francés");
        product1.setPrice(new BigDecimal(2500));
        product1.setDescription("Clásico pan francés");
        product1.setIngredients(Arrays.asList("Harina", "agua", "sal", "levadura"));
        product1.setImageUrl("https://example.com/images/baguette.jpg");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Muffin de arándanos");
        product2.setPrice(new BigDecimal(3500));
        product2.setDescription("Muffin suave con arándanos frescos");
        product2.setIngredients(Arrays.asList("Harina", "arándanos", "azúcar", "mantequilla", "huevos", "levadura"));
        product2.setImageUrl("https://example.com/images/muffin.jpg");

        when(productRepository.findAll()).thenReturn(java.util.List.of(product1, product2));

        // Act
        java.util.List<ProductResponseDTO> result = productService.getAllProducts();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);

        verify(productRepository).findAll();
    }
}
