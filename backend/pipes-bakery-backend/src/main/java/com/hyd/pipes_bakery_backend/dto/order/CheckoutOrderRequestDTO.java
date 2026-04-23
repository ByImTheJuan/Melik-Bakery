package com.hyd.pipes_bakery_backend.dto.order;


import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutOrderRequestDTO {

    @NotBlank(message = "Client first name is required")
    private String clientFirstName;

    @NotBlank(message = "Client last name is required")
    private String clientLastName;

    @NotBlank(message = "Client email is required")
    @Email(message = "Client email must be valid")
    private String clientEmail;

    @NotBlank(message = "Client phone number is required")
    private String clientPhoneNumber;

    @NotNull(message = "Shipping address is required")
    @Valid()
    private AddressSnapshotDTO shippingAddress;

    private String receiverName;

    
    public AddressSnapshotDTO getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressSnapshotDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
