package com.hyd.pipes_bakery_backend.dto.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;
import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.model.CartItemType;

/**
 * Everything needed to build an order once its payment is approved: the customer and
 * shipping data plus the cart contents and prices at the moment the checkout started.
 * Stored as JSON on each payment attempt so the order always matches the amount charged.
 */
public class CheckoutSnapshot {

    private CheckoutOrderRequestDTO request;
    private List<Item> items = new ArrayList<>();
    private BigDecimal shippingCost;

    public CheckoutSnapshot() {
    }

    public CheckoutSnapshot(CheckoutOrderRequestDTO request, List<Item> items, BigDecimal shippingCost) {
        this.request = request;
        this.items = items;
        this.shippingCost = shippingCost;
    }

    @JsonIgnore
    public BigDecimal getTotalAmount() {
        BigDecimal total = shippingCost != null ? shippingCost : BigDecimal.ZERO;
        for (Item item : items) {
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    public CheckoutOrderRequestDTO getRequest() {
        return request;
    }

    public void setRequest(CheckoutOrderRequestDTO request) {
        this.request = request;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public static class Item {

        private long productId;
        private int quantity;
        private BigDecimal unitPrice;
        // Both null on snapshots taken before custom cakes existed: those only hold products
        private CartItemType type;
        private String name;
        private CustomCakeDetails customCake;

        public Item() {
        }

        public Item(long productId, int quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public static Item customCake(String name, int quantity, BigDecimal unitPrice, CustomCakeDetails details) {
            Item item = new Item(0L, quantity, unitPrice);
            item.type = CartItemType.CUSTOM_CAKE;
            item.name = name;
            item.customCake = details;
            return item;
        }

        public CartItemType getType() {
            return type == null ? CartItemType.PRODUCT : type;
        }

        public void setType(CartItemType type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }
    }
}
