package com.hyd.pipes_bakery_backend.dto.payment;

import java.time.LocalDate;

import com.hyd.pipes_bakery_backend.model.DeliverySlot;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Nueva fecha de entrega para un reintento de pago, necesaria si la original ya no cumple el plazo minimo.")
public class RetryPaymentRequestDTO {

    @Schema(description = "Nueva fecha de entrega.", example = "2026-10-12")
    private LocalDate deliveryDate;

    @Schema(description = "Nueva franja horaria de entrega.", example = "MORNING")
    private DeliverySlot deliverySlot;

    public RetryPaymentRequestDTO() {
    }

    public RetryPaymentRequestDTO(LocalDate deliveryDate, DeliverySlot deliverySlot) {
        this.deliveryDate = deliveryDate;
        this.deliverySlot = deliverySlot;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public DeliverySlot getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(DeliverySlot deliverySlot) {
        this.deliverySlot = deliverySlot;
    }
}
