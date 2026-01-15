package com.hyd.pipes_bakery_backend.model;

public class Address {
    private final String street;
    private final String additionalInformation;
    private final String city;
    private final String state;
    private final int zipCode;
    private final String country;

    public Address(String street, String additionalInformation, String city, String state, int zipCode, String country) {
        this.street = street;
        this.additionalInformation = additionalInformation;
        this.city = city;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public int getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }
}
