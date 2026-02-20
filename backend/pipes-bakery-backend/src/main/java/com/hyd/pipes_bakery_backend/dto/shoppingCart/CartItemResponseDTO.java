package com.hyd.pipes_bakery_backend.dto.shoppingCart;

public class CartItemResponseDTO {

    private Long productId;
    private String productName;
    private double unitPrice;
    private int quantity;
    private double subtotal;

    public CartItemResponseDTO(Long productId, String productName, double unitPrice, int quantity, double subtotal) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getSubtotal() {
        return subtotal;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}