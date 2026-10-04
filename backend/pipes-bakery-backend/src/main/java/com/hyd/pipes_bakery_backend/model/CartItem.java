package com.hyd.pipes_bakery_backend.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;

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

    @Schema(description = "Nombre del fichero de imagen del producto.", example = "cinnamonRoll.jpg")
    private String productImage;

    @Schema(description = "Tipo de linea: producto del catalogo o torta personalizada.", example = "PRODUCT")
    private CartItemType type;

    @Schema(description = "Identificador de la linea, solo para tortas personalizadas.", example = "8c0f2c1e-3d5b-4f43-9a51-2a3f8b7e6d10")
    private String lineId;

    @Schema(description = "Detalle de la torta personalizada; nulo para productos del catalogo.")
    private CustomCakeDetails customCake;

    public CartItem() {}

    public CartItem(long productId, String productName, int quantity, BigDecimal unitPriceAtAdd, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPriceAtAdd = unitPriceAtAdd;
        this.productImage = productImage;
    }

    public static CartItem customCake(String lineId, String name, int quantity, BigDecimal unitPrice, CustomCakeDetails details) {
        CartItem item = new CartItem(0L, name, quantity, unitPrice, details.getImageFile());
        item.type = CartItemType.CUSTOM_CAKE;
        item.lineId = lineId;
        item.customCake = details;
        return item;
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

    // Carts stored before custom cakes existed have no type: they only hold products
    public CartItemType getType() {
        return type == null ? CartItemType.PRODUCT : type;
    }
    public void setType(CartItemType type) {
        this.type = type;
    }
    public String getLineId() {
        return lineId;
    }
    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
    public CustomCakeDetails getCustomCake() {
        return customCake;
    }
    public void setCustomCake(CustomCakeDetails customCake) {
        this.customCake = customCake;
    }
    @JsonIgnore
    public boolean isCustomCakeLine() {
        return getType() == CartItemType.CUSTOM_CAKE;
    }
}
