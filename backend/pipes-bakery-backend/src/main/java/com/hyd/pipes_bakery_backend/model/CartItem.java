package com.hyd.pipes_bakery_backend.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CartItem {

    private Long productId;

    private String productName;

    private int quantity;

    private BigDecimal unitPrice;

    public CartItem() {}

    public CartItem(long productId, String productName, int quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public int getQuantity() {
        return quantity;
    }
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    @JsonIgnore
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setProductId(long productId) {
        this.productId = productId;
    }

}
