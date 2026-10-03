package com.hyd.pipes_bakery_backend.dto.payment;

import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado de un intento de pago, consultado al volver de Wompi.")
public class PaymentStatusResponseDTO {

    public enum Status {
        PENDING,
        APPROVED,
        FAILED
    }

    @Schema(description = "Estado del intento de pago.", example = "APPROVED")
    private Status status;

    @Schema(description = "ID publico del pedido creado. Solo presente si el pago fue aprobado.", example = "AB12CD")
    private String orderId;

    @Schema(description = "Detalle del pedido creado, para mostrarlo en la confirmacion. Solo presente si el pago fue aprobado.")
    private OrderResponseDTO order;

    public PaymentStatusResponseDTO(Status status, String orderId) {
        this(status, orderId, null);
    }

    public PaymentStatusResponseDTO(Status status, String orderId, OrderResponseDTO order) {
        this.status = status;
        this.orderId = orderId;
        this.order = order;
    }

    public Status getStatus() {
        return status;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderResponseDTO getOrder() {
        return order;
    }
}
