package com.hyd.pipes_bakery_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeImageUploadResponseDTO;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeOptionsResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ApiError;
import com.hyd.pipes_bakery_backend.service.ICustomCakeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/custom-cakes")
@Tag(name = "Tortas personalizadas", description = "Catalogo de opciones y fotos del modulo de personalizacion de tortas.")
public class CustomCakeController {

    private final ICustomCakeService customCakeService;

    public CustomCakeController(ICustomCakeService customCakeService) {
        this.customCakeService = customCakeService;
    }

    // GET /api/custom-cakes/options
    @GetMapping("/options")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Opciones de personalizacion", description = "Devuelve tamanos, sabores, pisos, colores, elementos decorativos y sus precios.")
    @ApiResponse(responseCode = "200", description = "Catalogo de opciones",
            content = @Content(schema = @Schema(implementation = CustomCakeOptionsResponseDTO.class)))
    public CustomCakeOptionsResponseDTO getOptions() {
        return customCakeService.getOptions();
    }

    // POST /api/custom-cakes/images
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Subir foto para la torta",
            description = "Guarda la foto (JPG, PNG o WEBP) que el cliente quiere imprimir sobre su torta y devuelve la ruta a enviar como imageFile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Foto guardada",
                    content = @Content(schema = @Schema(implementation = CustomCakeImageUploadResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Fichero ausente o no es una imagen valida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "413", description = "La imagen supera el tamano maximo permitido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "429", description = "Demasiadas subidas desde esta IP",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public CustomCakeImageUploadResponseDTO uploadImage(
            @Parameter(description = "Foto a imprimir sobre la torta") @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        return new CustomCakeImageUploadResponseDTO(customCakeService.storeImage(file, request.getRemoteAddr()));
    }
}
