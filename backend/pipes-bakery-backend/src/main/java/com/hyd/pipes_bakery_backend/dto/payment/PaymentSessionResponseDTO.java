package com.hyd.pipes_bakery_backend.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sesion de pago creada para iniciar el checkout de Wompi.")
public class PaymentSessionResponseDTO {

    @Schema(description = "URL de Wompi Web Checkout a la que debe redirigirse al cliente.")
    private String checkoutUrl;

    @Schema(description = "Referencia unica de este intento de pago.")
    private String reference;

    public PaymentSessionResponseDTO(String checkoutUrl, String reference) {
        this.checkoutUrl = checkoutUrl;
        this.reference = reference;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public String getReference() {
        return reference;
    }
}
