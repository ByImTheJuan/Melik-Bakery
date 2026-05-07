package com.hyd.pipes_bakery_backend.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para crear o actualizar una direccion.")
public class AddressRequestDTO {

    @Schema(description = "Calle y numero de la direccion.", example = "Calle Mayor 12")
    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must be at most 100 characters")
    private String street;

    @Schema(description = "Informacion adicional para facilitar la entrega.", example = "Piso 2, puerta B")
    private String additionalInformation;

    @Schema(description = "Ciudad de la direccion.", example = "Madrid")
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @Schema(description = "Codigo postal de 6 digitos.", example = "110001")
    @NotNull(message = "Zip code is required")
    @Min(value = 100000, message = "Zip code must be exactly 6 characters")
    @Max(value = 999999, message = "Zip code must be exactly 6 characters")
    private int zipCode;

    @Schema(description = "Pais de la direccion.", example = "Spain")
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must be at most 100 characters")
    private String country;

    public String getStreet() {
        return street;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public String getCity() {
        return city;
    }

    public int getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
