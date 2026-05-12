package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import org.springframework.lang.NonNull;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Producto y cantidad que se quieren anadir al carrito.")
public class AddCartItemRequestDTO {

    @Schema(description = "Identificador del producto a anadir.", example = "3")
    @NotNull(message = "Product ID is required")
    private long productId;

    @Schema(description = "Cantidad de unidades a anadir.", example = "2")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public AddCartItemRequestDTO() {
    }
 
    public long getProductId() {
        return productId;
    }

    public void setProductId(@NonNull Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
