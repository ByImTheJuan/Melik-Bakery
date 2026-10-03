package com.hyd.pipes_bakery_backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.hyd.pipes_bakery_backend.config.ImageProperties;
import com.hyd.pipes_bakery_backend.exception.ImageStorageException;
import com.hyd.pipes_bakery_backend.exception.InvalidImageException;

@Service
public class ImageStorageService implements IImageStorageService {

    private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private static final int MAX_BASE_NAME_LENGTH = 50;

    private final ImageProperties imageProperties;

    public ImageStorageService(ImageProperties imageProperties) {
        this.imageProperties = imageProperties;
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Image file is required");
        }

        String extension = resolveExtension(file);
        String fileName = buildFileName(file.getOriginalFilename(), extension);

        try {
            Path directory = Paths.get(imageProperties.getPath()).toAbsolutePath().normalize();
            Path target = directory.resolve(fileName).normalize();

            if (!target.getParent().equals(directory)) {
                throw new InvalidImageException("Invalid image file name");
            }

            Files.createDirectories(directory);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new ImageStorageException("Could not store image file", ex);
        }

        return fileName;
    }

    private String resolveExtension(MultipartFile file) {
        String contentType = file.getContentType() == null
                ? ""
                : file.getContentType().toLowerCase(Locale.ROOT);
        String extension = EXTENSIONS_BY_CONTENT_TYPE.get(contentType);

        if (extension == null) {
            throw new InvalidImageException("Only JPG, PNG and WEBP images are allowed");
        }

        if (!hasImageSignature(file, contentType)) {
            throw new InvalidImageException("File content does not match an image of type " + contentType);
        }

        return extension;
    }

    private boolean hasImageSignature(MultipartFile file, String contentType) {
        byte[] header = new byte[12];

        try (InputStream inputStream = file.getInputStream()) {
            int read = inputStream.readNBytes(header, 0, header.length);
            header = Arrays.copyOf(header, read);
        } catch (IOException ex) {
            throw new ImageStorageException("Could not read image file", ex);
        }

        return switch (contentType) {
            case "image/jpeg" -> startsWith(header, 0, 0xFF, 0xD8, 0xFF);
            case "image/png" -> startsWith(header, 0, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A);
            case "image/webp" -> startsWith(header, 0, 'R', 'I', 'F', 'F') && startsWith(header, 8, 'W', 'E', 'B', 'P');
            default -> false;
        };
    }

    private static boolean startsWith(byte[] data, int offset, int... signature) {
        if (data.length < offset + signature.length) {
            return false;
        }

        for (int i = 0; i < signature.length; i++) {
            if ((data[offset + i] & 0xFF) != signature[i]) {
                return false;
            }
        }

        return true;
    }

    private String buildFileName(String originalFilename, String extension) {
        String baseName = StringUtils.stripFilenameExtension(StringUtils.getFilename(
                originalFilename == null ? "" : originalFilename.replace('\\', '/')));

        String sanitized = (baseName == null ? "" : baseName)
                .replaceAll("[^A-Za-z0-9_-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");

        if (sanitized.length() > MAX_BASE_NAME_LENGTH) {
            sanitized = sanitized.substring(0, MAX_BASE_NAME_LENGTH);
        }

        if (sanitized.isEmpty()) {
            sanitized = "product";
        }

        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return sanitized + "-" + suffix + "." + extension;
    }
}
