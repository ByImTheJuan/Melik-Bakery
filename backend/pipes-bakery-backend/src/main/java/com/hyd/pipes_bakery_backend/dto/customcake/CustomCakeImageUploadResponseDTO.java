package com.hyd.pipes_bakery_backend.dto.customcake;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado de subir la foto de una torta personalizada.")
public record CustomCakeImageUploadResponseDTO(
        @Schema(description = "Ruta relativa de la foto, a enviar como imageFile.", example = "custom-cakes/cake-1b2c3d4e.jpg")
        String imageFile
) {
}
