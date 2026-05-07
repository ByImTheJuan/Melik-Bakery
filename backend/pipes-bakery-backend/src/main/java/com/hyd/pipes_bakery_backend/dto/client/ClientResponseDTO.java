package com.hyd.pipes_bakery_backend.dto.client;

import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cliente devuelto por la API.")
public class ClientResponseDTO {

    @Schema(description = "Identificador unico del cliente.", example = "1")
    private Long id;
    @Schema(description = "Nombre del cliente.", example = "Maria")
    private String firstName;
    @Schema(description = "Apellidos del cliente.", example = "Garcia")
    private String lastName;
    @Schema(description = "Email del cliente.", example = "maria@example.com")
    private String email;
    @Schema(description = "Telefono de contacto del cliente.", example = "+34600111222")
    private String phoneNumber;
    @Schema(description = "Direccion principal asociada al cliente.")
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
