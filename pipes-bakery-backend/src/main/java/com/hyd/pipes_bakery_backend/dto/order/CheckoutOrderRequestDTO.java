package com.hyd.pipes_bakery_backend.dto.order;


import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CheckoutOrderRequestDTO {

    @NotNull(message = "Client information is required")
    @Valid()
    private ClientRequestDTO client;

    @NotNull(message = "Shipping address is required")
    @Valid()
    private AddressSnapshotDTO shippingAddress;

    
    public AddressSnapshotDTO getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressSnapshotDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public ClientRequestDTO getClient() {
        return client;
    }

    public void setClient(ClientRequestDTO client) {
        this.client = client;
    }
    
}
