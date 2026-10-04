package com.hyd.pipes_bakery_backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface IImageStorageService {

    /**
     * Stores an uploaded product image in the configured images directory.
     *
     * @return the generated file name (no path), to be saved as the product's imageFile
     */
    String store(MultipartFile file);

    /**
     * Stores an uploaded image inside a subdirectory of the images directory under a random name.
     *
     * @return the path relative to the images directory, e.g. {@code custom-cakes/cake-ab12.jpg}
     */
    String store(MultipartFile file, String subdirectory, String baseName);
}
