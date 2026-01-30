package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import org.springframework.lang.NonNull;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AddCartItemRequestDTO {

    @NonNull
    @NotBlank(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public AddCartItemRequestDTO(@NonNull Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public @NonNull Long getProductId() {
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
