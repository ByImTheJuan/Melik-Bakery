package com.hyd.pipes_bakery_backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StaticResourceConfigIntegrationTest {

    @TempDir
    static Path imageDirectory;

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void imageProperties(DynamicPropertyRegistry registry) {
        registry.add("app.images.path", imageDirectory::toString);
        registry.add("app.images.url-pattern", () -> "/images/products/**");
    }

    @BeforeAll
    static void createProductImage() throws IOException {
        Files.write(imageDirectory.resolve("sample.jpg"), new byte[] {1, 2, 3});
    }

    @Test
    void shouldServeAProductImageFromAFlatConfiguredDirectory() throws Exception {
        mockMvc.perform(get("/images/products/sample.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[] {1, 2, 3}));
    }
}
