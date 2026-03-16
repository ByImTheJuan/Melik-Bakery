package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Test
    void shouldCreateAndRetrieveProduct() {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Pan de chocolate");
        request.setPrice(new BigDecimal(4000));
        request.setDescription("Relleno de chocolate");
        request.setIngredients(Arrays.asList("Harina", "agua", "sal"));
        request.setImageUrl("https://example.com/images/baguette.jpg");

        ProductResponseDTO created = productService.createProduct(request);

        ProductResponseDTO found =
                productService.getProductById(created.getId());

        assertThat(found.getName()).isEqualTo("Pan de chocolate");
        assertThat(found.getPrice()).isEqualByComparingTo(new BigDecimal(4000));
        assertThat(found.getDescription()).isEqualTo("Relleno de chocolate");
        assertThat(found.getIngredients()).isEqualTo(Arrays.asList("Harina", "agua", "sal"));
        assertThat(found.getImageUrl()).isEqualTo("https://example.com/images/baguette.jpg");
    }
}
