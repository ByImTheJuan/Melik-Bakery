package com.hyd.pipes_bakery_backend.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class AddressSnapshot {

    @NotBlank
    private String street;

    private String additionalInformation;

    @NotBlank
    private String city;
    
    @NotBlank
    private int zipCode;
    
    @NotBlank
    private String country;

    protected AddressSnapshot() {}

    public AddressSnapshot(String street, String additionalInformation, String city, int zipCode, String country) {
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
}
