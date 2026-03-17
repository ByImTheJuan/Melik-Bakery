package com.hyd.pipes_bakery_backend.dto.shoppingCart;

public class CartItemResponseDTO {

    private Long productId;
    private String productName;
    private String productImage;
    private double unitPriceAtAdd;
    private int quantity;
    private double subtotal;

    public CartItemResponseDTO(Long productId, String productName, String productImage, double unitPriceAtAdd, int quantity, double subtotal) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.unitPriceAtAdd = unitPriceAtAdd;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public double getUnitPriceAtAdd() {
        return unitPriceAtAdd;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getSubtotal() {
        return subtotal;
    }
    public String getProductImage() {
        return productImage;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setUnitPriceAtAdd(double unitPriceAtAdd) {
        this.unitPriceAtAdd = unitPriceAtAdd;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}