package com.hyd.pipes_bakery_backend.dto.orderItem;

public class OrderItemResponseDTO {
        
    private long id;
    private long productId;
    private int quantity;
    private double unitPriceAtPurchase;

    public OrderItemResponseDTO(long id, long productId, int quantity, double unitPriceAtPurchase) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPriceAtPurchase = unitPriceAtPurchase;
    }

    public long getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getUnitPriceAtPurchase() {
        return unitPriceAtPurchase;
    }
    public long getId() {
        return id;
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
    public void setId(long id) {
        this.id = id;
    }
}
