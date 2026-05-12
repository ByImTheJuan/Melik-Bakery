package com.hyd.pipes_bakery_backend.dto.product;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Nuevo orden completo del catalogo de productos.")
public class ProductOrderRequestDTO {

    @Schema(description = "IDs de productos en el orden exacto en que deben mostrarse.", example = "[3, 1, 2]")
    @NotNull(message = "Product IDs are required")
    @NotEmpty(message = "Product IDs list cannot be empty")
    private List<@NotNull(message = "Product ID cannot be null") Long> productIds;

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
}
