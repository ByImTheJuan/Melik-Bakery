package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Nueva cantidad para una linea del carrito.")
public class UpdateCartItemQuantityRequestDTO {
    @Schema(description = "Cantidad final que debe tener el producto en el carrito.", example = "3")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
