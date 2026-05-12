package com.hyd.pipes_bakery_backend.dto.order;


import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos de cliente y envio usados para convertir un carrito en pedido.")
public class CheckoutOrderRequestDTO {

    @Schema(description = "Nombre del cliente que realiza el pedido.", example = "Maria")
    @NotBlank(message = "Client first name is required")
    private String clientFirstName;

    @Schema(description = "Apellidos del cliente que realiza el pedido.", example = "Garcia")
    @NotBlank(message = "Client last name is required")
    private String clientLastName;

    @Schema(description = "Email de contacto para el pedido.", example = "maria@example.com")
    @NotBlank(message = "Client email is required")
    @Email(message = "Client email must be valid")
    private String clientEmail;

    @Schema(description = "Telefono de contacto para el pedido.", example = "+34600111222")
    @NotBlank(message = "Client phone number is required")
    private String clientPhoneNumber;

    @Schema(description = "Direccion de envio capturada en el momento de confirmar el pedido.")
    @NotNull(message = "Shipping address is required")
    @Valid()
    private AddressSnapshotDTO shippingAddress;

    @Schema(description = "Nombre de la persona que recibira el pedido si es distinta del cliente.", example = "Lucia Garcia")
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
