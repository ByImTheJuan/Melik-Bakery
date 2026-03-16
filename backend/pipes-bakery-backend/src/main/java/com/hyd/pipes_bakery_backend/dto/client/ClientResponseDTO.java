package com.hyd.pipes_bakery_backend.dto.client;

import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;

public class ClientResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private AddressResponseDTO address;

    public ClientResponseDTO(Long id, String firstName, String lastName, String email, String phoneNumber, AddressResponseDTO address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public AddressResponseDTO getAddress() {
        return address;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(AddressResponseDTO address) {
        this.address = address;
    }
}
