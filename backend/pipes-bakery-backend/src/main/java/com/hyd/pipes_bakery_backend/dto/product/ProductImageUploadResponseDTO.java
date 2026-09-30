package com.hyd.pipes_bakery_backend.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado de la subida de una imagen de producto.")
public class ProductImageUploadResponseDTO {

    @Schema(description = "Nombre del fichero guardado, a usar como imageFile del producto.", example = "cinnamonRoll-3f2a9c1b.jpg")
    private String imageFile;

    public ProductImageUploadResponseDTO() {
    }

    public ProductImageUploadResponseDTO(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
