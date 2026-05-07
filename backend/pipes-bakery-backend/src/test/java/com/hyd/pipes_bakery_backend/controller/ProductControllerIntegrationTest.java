package com.hyd.pipes_bakery_backend.controller;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndRetrieveProduct() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Pan de chocolate");
        request.setPrice(new BigDecimal(4000));
        request.setDescription("Relleno de chocolate");
        request.setIngredients(Arrays.asList("Harina", "chocolate"));
        request.setImageUrl("https://example.com/images/baguette.jpg");

        MvcResult creationResult = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andReturn();

        ProductResponseDTO createdProduct = objectMapper.readValue(
                creationResult.getResponse().getContentAsString(),
                ProductResponseDTO.class
        );

        mockMvc.perform(get("/api/products/{id}", createdProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Pan de chocolate"))
            .andExpect(jsonPath("$.price").value(4000))
            .andExpect(jsonPath("$.description").value("Relleno de chocolate"))
            .andExpect(jsonPath("$.ingredients").isArray())
            .andExpect(jsonPath("$.ingredients[0]").value("Harina"))
            .andExpect(jsonPath("$.ingredients[1]").value("chocolate"))
            .andExpect(jsonPath("$.imageUrl").value("https://example.com/images/baguette.jpg"));
    }
}
