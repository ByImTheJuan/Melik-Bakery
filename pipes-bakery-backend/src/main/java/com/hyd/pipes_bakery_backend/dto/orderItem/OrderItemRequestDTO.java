package com.hyd.pipes_bakery_backend.dto.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class OrderItemRequestDTO {

    @NotBlank(message = "Product ID is required")
    private long productId;

    @NotBlank(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @NotBlank(message = "Unit price at purchase is required")
    @Min(value = 0, message = "Unit price at purchase must be at least 0")
    private double unitPriceAtPurchase;


    public long getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getUnitPriceAtPurchase() {
        return unitPriceAtPurchase;
    }
    public void setProductId(long productId) {
        this.productId = productId;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setUnitPriceAtPurchase(double unitPriceAtPurchase) {
        this.unitPriceAtPurchase = unitPriceAtPurchase;
    }
}
