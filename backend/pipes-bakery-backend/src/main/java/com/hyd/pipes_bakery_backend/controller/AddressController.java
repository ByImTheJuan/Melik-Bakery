package com.hyd.pipes_bakery_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ApiError;
import com.hyd.pipes_bakery_backend.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Direcciones", description = "Gestion de direcciones de clientes.")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // GET /api/addresses
    @GetMapping
    @Operation(summary = "Listar direcciones", description = "Devuelve todas las direcciones registradas.")
    @ApiResponse(responseCode = "200", description = "Listado de direcciones",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AddressResponseDTO.class))))
    public List<AddressResponseDTO> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    // GET /api/addresses/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Obtener direccion por ID", description = "Devuelve una direccion concreta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Direccion encontrada",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Direccion no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public AddressResponseDTO getAddressById(@Parameter(description = "ID de la direccion", example = "1") @NonNull @PathVariable Long id) {
        return addressService.getAddressById(id);
    }

    // POST /api/addresses
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear direccion", description = "Registra una nueva direccion.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Direccion creada",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public AddressResponseDTO createAddress(@NonNull @Valid @RequestBody AddressRequestDTO address) {
        return addressService.createAddress(address);
    }

    // UPDATE
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar direccion", description = "Actualiza una direccion existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Direccion actualizada",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Direccion no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public AddressResponseDTO updateAddress(@Parameter(description = "ID de la direccion", example = "1") @NonNull @PathVariable Long id, @Valid @RequestBody AddressRequestDTO updatedAddress) {
        return addressService.updateAddress(id, updatedAddress);
    }

    // DELETE /api/addresses/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar direccion", description = "Elimina una direccion existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Direccion eliminada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Direccion no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public void deleteAddress(@Parameter(description = "ID de la direccion", example = "1") @NonNull @PathVariable Long id) {
        addressService.deleteAddress(id);
    }
}
