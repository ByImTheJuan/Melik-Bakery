package com.hyd.pipes_bakery_backend.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

    // Name at purchase time; the only name a personalized cake has (it has no product)
    @Column(name = "item_name", length = 120)
    private String itemName;

    @Convert(converter = CustomCakeDetailsConverter.class)
    @Column(name = "custom_cake_details", columnDefinition = "TEXT")
    private CustomCakeDetails customCakeDetails;

    public OrderItem() {
    }

    public static OrderItem customCake(String name, int quantity, BigDecimal unitPriceAtPurchase, CustomCakeDetails details) {
        OrderItem item = new OrderItem();
        item.itemName = name;
        item.quantity = quantity;
        item.unitPriceAtPurchase = unitPriceAtPurchase;
        item.customCakeDetails = details;
        return item;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public CustomCakeDetails getCustomCakeDetails() {
        return customCakeDetails;
    }

    public void setCustomCakeDetails(CustomCakeDetails customCakeDetails) {
        this.customCakeDetails = customCakeDetails;
    }

    @JsonIgnore
    public boolean isCustomCake() {
        return customCakeDetails != null;
    }

    @JsonIgnore
    public BigDecimal calculateTotalPrice() {
        return unitPriceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }
}
