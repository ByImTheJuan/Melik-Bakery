package com.hyd.pipes_bakery_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6, updatable = false)
    private String publicId;

    // Quién hizo el pedido
    @Column(nullable = false)
    private String clientFirstName;

    @Column(nullable = false)
    private String clientLastName;

    @Column(nullable = false)
    private String clientEmail;

    @Column(nullable = false)
    private String clientPhoneNumber;

    // Dirección de envío
    @Embedded
    private AddressSnapshot shippingAddress;

    // Ítems comprados (snapshot)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private BigDecimal shippingCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private String receiverName;

    // Chosen by the customer at checkout; null on orders placed before delivery dates existed
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_slot", length = 20)
    private DeliverySlot deliverySlot;

    protected Order() {}

    public Order(String clientFirstName, String clientLastName, String clientEmail, String clientPhoneNumber, AddressSnapshot shippingAddress, String receiverName) {
        this(clientFirstName, clientLastName, clientEmail, clientPhoneNumber, shippingAddress, receiverName, BigDecimal.ZERO);
    }

    public Order(String clientFirstName, String clientLastName, String clientEmail, String clientPhoneNumber, AddressSnapshot shippingAddress, String receiverName, BigDecimal shippingCost) {
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.clientEmail = clientEmail;
        this.clientPhoneNumber = clientPhoneNumber;
        this.shippingAddress = shippingAddress;
        this.receiverName = receiverName;
        this.shippingCost = shippingCost != null ? shippingCost : BigDecimal.ZERO;
        this.status = OrderStatus.PAYMENT_PENDING;
        this.createdAt = LocalDateTime.now();
        this.totalAmount = this.shippingCost;
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public AddressSnapshot getAddress() {
        return shippingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public void setShippingAddress(AddressSnapshot shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        recalculateTotalAmount();
    }

    private BigDecimal recalculateTotalAmount() {
        BigDecimal subtotal = new BigDecimal(0);
        for (OrderItem item : items){
            subtotal = item.calculateTotalPrice().add(subtotal);
        }

        BigDecimal shipping = shippingCost != null ? shippingCost : BigDecimal.ZERO;
        this.totalAmount = subtotal.add(shipping);
        return totalAmount;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public DeliverySlot getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(DeliverySlot deliverySlot) {
        this.deliverySlot = deliverySlot;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        this.totalAmount = this.totalAmount.add(item.getUnitPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));
    }
}
