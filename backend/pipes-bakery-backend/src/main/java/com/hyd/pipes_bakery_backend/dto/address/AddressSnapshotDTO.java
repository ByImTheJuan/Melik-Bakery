package com.hyd.pipes_bakery_backend.dto.address;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddressSnapshotDTO {

    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must be at most 100 characters")
    private String street;

    private String additionalInformation;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @NotNull(message = "Zip code is required")
    @Min(value = 110000, message = "Zip code must have 6 digits and start with 11")
    @Max(value = 119999, message = "Zip code must have 6 digits and start with 11")
    private int zipCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must be at most 100 characters")
    private String country;

    public AddressSnapshotDTO() {
    }
    public AddressSnapshotDTO(String street, String additionalInformation, String city, int zipCode,
            String country) {
        this.street = street;
        this.additionalInformation = additionalInformation;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
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
