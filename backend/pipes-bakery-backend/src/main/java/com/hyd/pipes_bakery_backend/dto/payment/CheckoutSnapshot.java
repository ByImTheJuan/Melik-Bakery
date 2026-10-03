package com.hyd.pipes_bakery_backend.dto.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;

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

        public Item() {
        }

        public Item(long productId, int quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
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
