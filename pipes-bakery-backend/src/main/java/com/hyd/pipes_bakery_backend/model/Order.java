package com.hyd.pipes_bakery_backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quién hizo el pedido
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    // Dirección de envío
    @Embedded
    private AddressSnapshot shippingAddress;

    // Ítems comprados (snapshot)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Order() {}

    public Order(Client client, AddressSnapshot shippingAddress) {
        this.client = client;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.totalAmount = 0.0;
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public AddressSnapshot getAddress() {
        return shippingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setShippingAddress(AddressSnapshot shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        this.totalAmount = this.totalAmount + item.getUnitPriceAtPurchase() * item.getQuantity();
    }
}
