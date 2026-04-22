package com.hyd.pipes_bakery_backend.dto.shoppingCart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.hyd.pipes_bakery_backend.model.CartItem;

public class ShoppingCartResponseDTO {

    private UUID cartId;
    private List<CartItem> items;
    private BigDecimal shippingCost;
    private BigDecimal itemsTotal;
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
