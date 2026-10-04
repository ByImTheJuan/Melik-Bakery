package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import com.hyd.pipes_bakery_backend.config.ImageProperties;
import com.hyd.pipes_bakery_backend.exception.InvalidImageException;

class ImageStorageServiceTest {

    private static final byte[] PNG_BYTES = {
            (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0x0D
    };
    private static final byte[] JPEG_BYTES = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10};
    private static final byte[] WEBP_BYTES = {'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P'};

    @TempDir
    Path tempDir;

    private Path imageDirectory;
    private ImageStorageService imageStorageService;

    @BeforeEach
    void setUp() {
        // Nested, not-yet-existing directory: the service must create it.
        imageDirectory = tempDir.resolve("products-images");
        ImageProperties properties = new ImageProperties();
        properties.setPath(imageDirectory.toString());
        imageStorageService = new ImageStorageService(properties);
    }

    @Test
    void shouldStorePngAndReturnPlainFileName() throws IOException {
        String fileName = imageStorageService.store(
                new MockMultipartFile("file", "Cinnamon Roll.png", "image/png", PNG_BYTES));

        assertThat(fileName).matches("Cinnamon-Roll-[0-9a-f]{8}\\.png");
        assertThat(fileName).doesNotContain("/");
        assertThat(Files.readAllBytes(imageDirectory.resolve(fileName))).isEqualTo(PNG_BYTES);
    }

    @Test
    void shouldStoreJpegAndWebpWithNormalizedExtension() {
        String jpeg = imageStorageService.store(
                new MockMultipartFile("file", "cake.JPEG", "image/jpeg", JPEG_BYTES));
        String webp = imageStorageService.store(
                new MockMultipartFile("file", "brookie.webp", "image/webp", WEBP_BYTES));

        assertThat(jpeg).matches("cake-[0-9a-f]{8}\\.jpg");
        assertThat(webp).matches("brookie-[0-9a-f]{8}\\.webp");
        assertThat(imageDirectory.resolve(jpeg)).exists();
        assertThat(imageDirectory.resolve(webp)).exists();
    }

    @Test
    void shouldStoreInSubdirectoryWithoutKeepingTheOriginalName() throws IOException {
        String path = imageStorageService.store(
                new MockMultipartFile("file", "Foto de Ana.png", "image/png", PNG_BYTES), "custom-cakes", "cake");

        assertThat(path).matches("custom-cakes/cake-[0-9a-f]{32}\\.png");
        assertThat(path).doesNotContain("Ana");
        assertThat(Files.readAllBytes(imageDirectory.resolve(path))).isEqualTo(PNG_BYTES);
    }

    @Test
    void shouldRejectUnsafeSubdirectories() {
        MockMultipartFile file = new MockMultipartFile("file", "cake.png", "image/png", PNG_BYTES);

        assertThatThrownBy(() -> imageStorageService.store(file, "../outside", "cake"))
                .isInstanceOf(InvalidImageException.class);
    }

    @Test
    void shouldGenerateDifferentNamesForSameOriginalFile() {
        MockMultipartFile file = new MockMultipartFile("file", "cake.png", "image/png", PNG_BYTES);

        assertThat(imageStorageService.store(file)).isNotEqualTo(imageStorageService.store(file));
    }

    @Test
    void shouldKeepTraversalStyleNamesInsideImageDirectory() throws IOException {
        String fileName = imageStorageService.store(
                new MockMultipartFile("file", "../../etc/evil.png", "image/png", PNG_BYTES));

        assertThat(fileName).matches("evil-[0-9a-f]{8}\\.png");
        try (var files = Files.list(imageDirectory)) {
            assertThat(files).containsExactly(imageDirectory.resolve(fileName));
        }
    }

    @Test
    void shouldFallBackToDefaultBaseNameWhenOriginalNameIsUnusable() {
        String fileName = imageStorageService.store(
                new MockMultipartFile("file", "###.png", "image/png", PNG_BYTES));

        assertThat(fileName).matches("product-[0-9a-f]{8}\\.png");
    }

    @Test
    void shouldRejectEmptyFile() {
        assertThatThrownBy(() -> imageStorageService.store(
                new MockMultipartFile("file", "cake.png", "image/png", new byte[0])))
                .isInstanceOf(InvalidImageException.class)
                .hasMessage("Image file is required");
    }

    @Test
    void shouldRejectUnsupportedContentType() {
        assertThatThrownBy(() -> imageStorageService.store(
                new MockMultipartFile("file", "cake.gif", "image/gif", new byte[] {'G', 'I', 'F', '8'})))
                .isInstanceOf(InvalidImageException.class)
                .hasMessage("Only JPG, PNG and WEBP images are allowed");
    }

    @Test
    void shouldRejectFileWhoseContentDoesNotMatchDeclaredType() {
        assertThatThrownBy(() -> imageStorageService.store(
                new MockMultipartFile("file", "script.png", "image/png", "<script>".getBytes())))
                .isInstanceOf(InvalidImageException.class);

        assertThat(imageDirectory).doesNotExist();
    }
}
