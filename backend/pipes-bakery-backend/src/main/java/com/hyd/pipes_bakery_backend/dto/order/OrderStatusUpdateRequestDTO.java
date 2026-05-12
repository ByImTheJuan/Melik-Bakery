package com.hyd.pipes_bakery_backend.dto.order;

import com.hyd.pipes_bakery_backend.model.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Nuevo estado que se quiere asignar a un pedido.")
public class OrderStatusUpdateRequestDTO {

    @Schema(description = "Estado destino del pedido.", example = "SHIPPED")
    @NotNull(message = "Order status is required")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
