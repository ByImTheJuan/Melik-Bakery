package com.hyd.pipes_bakery_backend.dto.orderItem;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Linea de pedido solicitada.")
public class OrderItemRequestDTO {

    @Schema(description = "Identificador del producto.", example = "3")
    @NotBlank(message = "Product ID is required")
    private long productId;

    @Schema(description = "Cantidad de unidades del producto.", example = "2")
    @NotBlank(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Schema(description = "Precio unitario registrado en el momento de la compra.", example = "4.50")
    @NotBlank(message = "Unit price at purchase is required")
    @Min(value = 0, message = "Unit price at purchase must be at least 0")
    private BigDecimal unitPriceAtPurchase;


    public long getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public BigDecimal getUnitPriceAtPurchase() {
        return unitPriceAtPurchase;
    }
    public void setProductId(long productId) {
        this.productId = productId;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setUnitPriceAtPurchase(BigDecimal unitPriceAtPurchase) {
        this.unitPriceAtPurchase = unitPriceAtPurchase;
    }
}
