package com.hyd.pipes_bakery_backend.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto almacenado dentro de un carrito.")
public class CartItem {

    @Schema(description = "Identificador del producto.", example = "3")
    private long productId;

    @Schema(description = "Nombre del producto.", example = "Cinnamon Roll")
    private String productName;

    @Schema(description = "Cantidad del producto en el carrito.", example = "2")
    private int quantity;

    @Schema(description = "Precio unitario en el momento de anadirlo al carrito.", example = "4.50")
    private BigDecimal unitPriceAtAdd;

    @Schema(description = "URL o ruta publica de la imagen del producto.", example = "/images/products/cinnamonRoll.jpg")
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
