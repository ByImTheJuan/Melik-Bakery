package com.hyd.pipes_bakery_backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
    }

    @BeforeAll
    static void createProductImage() throws IOException {
        Files.write(imageDirectory.resolve("sample.jpg"), new byte[] {1, 2, 3});
        Files.createDirectories(imageDirectory.resolve("custom-cakes"));
        Files.write(imageDirectory.resolve("custom-cakes").resolve("photo.jpg"), new byte[] {4, 5, 6});
    }

    @Test
    void shouldServeAProductImageFromAFlatConfiguredDirectory() throws Exception {
        mockMvc.perform(get("/images/sample.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[] {1, 2, 3}));
    }

    @Test
    void shouldLetBrowsersAndTheCdnCacheProductImages() throws Exception {
        mockMvc.perform(get("/images/sample.jpg"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "max-age=31536000, public, immutable"));
    }

    @Test
    void shouldKeepCustomCakePhotosOutOfSharedCaches() throws Exception {
        mockMvc.perform(get("/images/custom-cakes/photo.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[] {4, 5, 6}))
                .andExpect(header().string("Cache-Control", "max-age=86400, private"));
    }
}
