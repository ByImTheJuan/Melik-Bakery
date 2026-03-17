package com.hyd.pipes_bakery_backend.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CartItem {

    private long productId;

    private String productName;

    private int quantity;

    private BigDecimal unitPriceAtAdd;

    private String productImage;

    public CartItem() {}

    public CartItem(long productId, String productName, int quantity, BigDecimal unitPriceAtAdd, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPriceAtAdd = unitPriceAtAdd;
        this.productImage = productImage;
    }

    public long getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public int getQuantity() {
        return quantity;
    }
    public BigDecimal getUnitPriceAtAdd() {
        return unitPriceAtAdd;
    }
    @JsonIgnore
    public BigDecimal getTotalPrice() {
        return unitPriceAtAdd.multiply(BigDecimal.valueOf(quantity));
    }
    public String getProductImage() {
        return productImage;
    }
    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setUnitPriceAtAdd(BigDecimal unitPriceAtAdd) {
        this.unitPriceAtAdd = unitPriceAtAdd;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setProductId(long productId) {
        this.productId = productId;
    }
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
