package com.hyd.pipes_bakery_backend.dto.orderItem;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Linea de pedido devuelta por la API.")
public class OrderItemResponseDTO {

    @Schema(description = "Identificador unico de la linea de pedido.", example = "10")
    private long id;
    @Schema(description = "Identificador del producto comprado.", example = "3")
    private long productId;
    @Schema(description = "Nombre del producto comprado.", example = "Cinnamon Roll")
    private String productName;
    @Schema(description = "Cantidad comprada.", example = "2")
    private int quantity;
    @Schema(description = "Precio unitario aplicado en el momento de la compra.", example = "4.50")
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
