package com.hyd.pipes_bakery_backend.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Indica unicamente si un pedido todavia admite un intento de pago.")
public class PayableResponseDTO {

    @Schema(description = "true si el pedido sigue pendiente de pago y puede reintentarse.")
    private boolean payable;

    public PayableResponseDTO(boolean payable) {
        this.payable = payable;
    }

    public boolean isPayable() {
        return payable;
    }
}
