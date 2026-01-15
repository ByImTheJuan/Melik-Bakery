package com.hyd.pipes_bakery_backend.model;

public class Client {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final Address address;

    public Client(String firstName, String lastName, String email, String phoneNumber, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
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

    public Address getAddress() {
        return address;
    }
}
