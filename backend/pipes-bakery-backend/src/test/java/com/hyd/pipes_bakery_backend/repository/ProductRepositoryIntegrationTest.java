package com.hyd.pipes_bakery_backend.repository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hyd.pipes_bakery_backend.model.Product;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndFindProduct() {
        Product product = new Product();
        product.setName("Baguette");
        product.setPrice(new BigDecimal(3000));
        product.setDescription("Pan artesanal");
        product.setIngredients("Harina, agua, sal");

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();

        Product found = productRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("Baguette");
        assertThat(found.getPrice()).isEqualTo(3000);
    }
}
