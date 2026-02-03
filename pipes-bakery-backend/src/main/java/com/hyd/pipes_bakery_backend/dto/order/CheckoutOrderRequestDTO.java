package com.hyd.pipes_bakery_backend.dto.order;


import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CheckoutOrderRequestDTO {

    @NotNull(message = "Shipping address is required")
    @Valid()
    private AddressSnapshotDTO shippingAddress;

    
    public AddressSnapshotDTO getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressSnapshotDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
