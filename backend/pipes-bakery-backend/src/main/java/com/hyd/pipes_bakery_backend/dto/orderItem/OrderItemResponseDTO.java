package com.hyd.pipes_bakery_backend.dto.orderItem;

import java.math.BigDecimal;

public class OrderItemResponseDTO {
        
    private long id;
    private long productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPriceAtPurchase;

    public OrderItemResponseDTO(long id, long productId, String productName, int quantity, BigDecimal unitPriceAtPurchase) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPriceAtPurchase = unitPriceAtPurchase;
    }

    public long getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public BigDecimal getUnitPriceAtPurchase() {
        return unitPriceAtPurchase;
    }
    public long getId() {
        return id;
    }
    public String getProductName(){
        return productName;
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
    public void setId(long id) {
        this.id = id;
    }
    public void setProductName(String productName){
        this.productName = productName;
    }
}
