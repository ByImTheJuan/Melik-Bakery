package com.hyd.pipes_bakery_backend.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private BigDecimal unitPriceAtPurchase;

    public OrderItem() {
    }

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPriceAtPurchase = product.getPrice();
    }

    public OrderItem(Product product, int quantity, BigDecimal unitPriceAtPurchase) {
        this.product = product;
        this.quantity = quantity;
        this.unitPriceAtPurchase = unitPriceAtPurchase;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPriceAtPurchase() {
        return unitPriceAtPurchase;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @JsonIgnore
    public BigDecimal calculateTotalPrice() {
        return unitPriceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }
}
