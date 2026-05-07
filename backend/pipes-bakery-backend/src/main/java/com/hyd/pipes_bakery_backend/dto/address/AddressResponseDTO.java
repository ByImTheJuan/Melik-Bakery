package com.hyd.pipes_bakery_backend.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Direccion devuelta por la API.")
public class AddressResponseDTO {

    @Schema(description = "Identificador unico de la direccion.", example = "1")
    private Long id;
    @Schema(description = "Calle y numero de la direccion.", example = "Calle Mayor 12")
    private String street;
    @Schema(description = "Informacion adicional para la entrega.", example = "Piso 2, puerta B")
    private String additionalInformation;
    @Schema(description = "Ciudad de la direccion.", example = "Madrid")
    private String city;
    @Schema(description = "Codigo postal de 6 digitos.", example = "110001")
    private int zipCode;
    @Schema(description = "Pais de la direccion.", example = "Spain")
    private String country;

    public AddressResponseDTO(Long id, String street, String additionalInformation, String city, int zipCode, String country) {
        this.id = id;
        this.street = street;
        this.additionalInformation = additionalInformation;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

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

    public void setId(Long id) {
        this.id = id;
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
