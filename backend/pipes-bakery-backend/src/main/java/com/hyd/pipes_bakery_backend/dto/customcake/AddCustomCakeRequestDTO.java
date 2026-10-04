package com.hyd.pipes_bakery_backend.dto.customcake;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Torta personalizada y cantidad que se quieren anadir al carrito.")
public class AddCustomCakeRequestDTO {

    @Schema(description = "Configuracion de la torta.")
    @NotNull(message = "Cake configuration is required")
    @Valid
    private CustomCakeConfigurationDTO configuration;

    @Schema(description = "Cantidad de tortas iguales.", example = "1")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10, message = "Quantity must be at most 10")
    private int quantity = 1;

    public AddCustomCakeRequestDTO() {
    }

    public CustomCakeConfigurationDTO getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CustomCakeConfigurationDTO configuration) {
        this.configuration = configuration;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
