package com.hyd.pipes_bakery_backend.dto.order;

import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class OrderRequestDTO {

    @NotNull(message = "Client ID is required")
    private long idClient;

    @NotNull(message = "Shipping address is required")
    @Valid()
    private AddressSnapshotDTO shippingAddress;


    public long getIdClient() {
        return idClient;
    }
    
    public AddressSnapshotDTO getShippingAddress() {
        return shippingAddress;
    }

    public void setIdClient(long idClient) {
        this.idClient = idClient;
    }

    public void setShippingAddress(AddressSnapshotDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
