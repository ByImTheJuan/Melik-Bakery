package com.hyd.pipes_bakery_backend.controller;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.service.ProductService;

@SuppressWarnings("null")
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateProductSuccessfully() throws Exception {

        // Arrange
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Baguette");
        request.setPrice(new BigDecimal(3000));
        request.setDescription("Pan artesanal");
        request.setIngredients(Arrays.asList("Harina", "agua", "sal"));
        request.setImageUrl("https://example.com/images/baguette.jpg");

        ProductResponseDTO response = new ProductResponseDTO(
                1L,
                "Baguette",
                "Pan artesanal",
                new BigDecimal(3000),
                Arrays.asList("Harina", "agua", "sal"),
                "https://example.com/images/baguette.jpg"
        );

        when(productService.createProduct(any(ProductRequestDTO.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Baguette"))
                .andExpect(jsonPath("$.price").value(3000))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(productService).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingProductWithInvalidData() throws Exception {

        // Arrange
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName(""); // Invalid name
        request.setPrice(new BigDecimal(-100)); // Invalid price
        request.setDescription("Pan artesanal");
        request.setIngredients(Arrays.asList("Harina", "agua", "sal"));
        request.setImageUrl("https://example.com/images/baguette.jpg");

        // Act + Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details").isNotEmpty());
    }

    @Test
    void shouldGetProductByIdSuccessfully() throws Exception {

        // Arrange
        Long productId = 1L;
        ProductResponseDTO response = new ProductResponseDTO(
                productId,
                "Baguette",
                "Pan artesanal",
                new BigDecimal(3000),
                Arrays.asList("Harina", "agua", "sal"),
                "https://example.com/images/baguette.jpg"
        );

        when(productService.getProductById(productId)).thenReturn(response);

        // Act + Assert
        mockMvc.perform(get("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Baguette"))
                .andExpect(jsonPath("$.price").value(3000))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(productService).getProductById(productId);
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistingProduct() throws Exception {

        // Arrange
        Long productId = 999L;

        when(productService.getProductById(productId))
                        .thenThrow(new ResourceNotFoundException("Product not found with id " + productId));

        // Act + Assert
        mockMvc.perform(get("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Product not found with id " + productId));

        verify(productService).getProductById(productId);
    }

    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        // Arrange
        Long productId = 1L;
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Baguette");
        request.setPrice(new BigDecimal(3000));
        request.setDescription("Pan artesanal");
        request.setIngredients(Arrays.asList("Harina", "agua", "sal"));
        request.setImageUrl("https://example.com/images/baguette.jpg");

        ProductResponseDTO response = new ProductResponseDTO(
                productId,
                "Baguette",
                "Pan artesanal",
                new BigDecimal(3000),
                Arrays.asList("Harina", "agua", "sal"),
                "https://example.com/images/baguette.jpg"
        );

        when(productService.updateProduct(anyLong(), any(ProductRequestDTO.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Baguette"))
                .andExpect(jsonPath("$.price").value(3000))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(productService).updateProduct(anyLong(), any(ProductRequestDTO.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingProduct() throws Exception {
        // Arrange
        Long productId = 999L;
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Baguette");
        request.setPrice(new BigDecimal(3000));
        request.setDescription("Pan artesanal");
        request.setIngredients(Arrays.asList("Harina", "agua", "sal"));
        request.setImageUrl("https://example.com/images/baguette.jpg");

        when(productService.updateProduct(anyLong(), any(ProductRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id " + productId));

        // Act + Assert
        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Product not found with id " + productId));

        verify(productService).updateProduct(anyLong(), any(ProductRequestDTO.class));
    }

    @Test
    void shouldDeleteProductSuccessfully() throws Exception {
        // Arrange
        Long productId = 1L;

        // Act + Assert
        mockMvc.perform(delete("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(productId);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingProduct() throws Exception {
        // Arrange
        Long productId = 999L;

        doThrow(new ResourceNotFoundException("Product not found with id " + productId))
                .when(productService).deleteProduct(productId);

        // Act + Assert
        mockMvc.perform(delete("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Product not found with id " + productId));

        verify(productService).deleteProduct(productId);
    }
}
