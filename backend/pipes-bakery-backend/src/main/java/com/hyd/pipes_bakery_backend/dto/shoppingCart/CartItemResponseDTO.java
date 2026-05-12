package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Linea de carrito devuelta por la API.")
public class CartItemResponseDTO {

    @Schema(description = "Identificador del producto.", example = "3")
    private Long productId;
    @Schema(description = "Nombre del producto.", example = "Cinnamon Roll")
    private String productName;
    @Schema(description = "URL o ruta publica de la imagen del producto.", example = "/images/products/cinnamonRoll.jpg")
    private String productImage;
    @Schema(description = "Precio unitario en el momento de anadirlo al carrito.", example = "4.50")
    private double unitPriceAtAdd;
    @Schema(description = "Cantidad de unidades en el carrito.", example = "2")
    private int quantity;
    @Schema(description = "Subtotal de esta linea.", example = "9.00")
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
