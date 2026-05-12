package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.hyd.pipes_bakery_backend.model.CartItem;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Carrito de compra con sus lineas e importes calculados.")
public class ShoppingCartResponseDTO {

    @Schema(description = "Identificador UUID del carrito.", example = "f4a9b6de-0c5d-4cb2-9a47-8dc413951f0f")
    private UUID cartId;
    @Schema(description = "Productos actualmente incluidos en el carrito.")
    private List<CartItem> items;
    @Schema(description = "Coste de envio calculado.", example = "3.99")
    private BigDecimal shippingCost;
    @Schema(description = "Total de los productos sin incluir envio.", example = "18.00")
    private BigDecimal itemsTotal;
    @Schema(description = "Total del carrito incluyendo envio.", example = "21.99")
    private BigDecimal totalPrice;

    public ShoppingCartResponseDTO(UUID cartId, List<CartItem> items,
        BigDecimal shippingCost, BigDecimal itemsTotal, BigDecimal totalPrice) {

        this.cartId = cartId;
        this.items = items;
        this.shippingCost = shippingCost;
        this.itemsTotal = itemsTotal;
        this.totalPrice = totalPrice;
    }

    public UUID getcartId() {
        return cartId;
    }
    public List<CartItem> getItems() {
        return items;
    }
    public BigDecimal getShippingCost() {
        return shippingCost;
    }
    public BigDecimal getItemsTotal() {
        return itemsTotal;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setcartId(UUID cartId) {
        this.cartId = cartId;
    }
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }
    public void setItemsTotal(BigDecimal itemsTotal) {
        this.itemsTotal = itemsTotal;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

}
