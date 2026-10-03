package com.hyd.pipes_bakery_backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Null until the payment is approved and the order is created from checkoutData
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "cart_id", length = 36, updatable = false)
    private String cartId;

    @Column(name = "checkout_data", columnDefinition = "TEXT", updatable = false)
    private String checkoutData;

    @Column(name = "wompi_reference", nullable = false, unique = true, updatable = false)
    private String wompiReference;

    @Column(name = "wompi_transaction_id", unique = true)
    private String wompiTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentTransactionStatus status;

    @Column(nullable = false, updatable = false)
    private Long amountInCents;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected PaymentTransaction() {
    }

    public PaymentTransaction(String cartId, String wompiReference, Long amountInCents, String checkoutData) {
        this.cartId = cartId;
        this.checkoutData = checkoutData;
        this.wompiReference = wompiReference;
        this.amountInCents = amountInCents;
        this.status = PaymentTransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public String getCartId() {
        return cartId;
    }

    public String getCheckoutData() {
        return checkoutData;
    }

    public String getWompiReference() {
        return wompiReference;
    }

    public String getWompiTransactionId() {
        return wompiTransactionId;
    }

    public PaymentTransactionStatus getStatus() {
        return status;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.updatedAt = LocalDateTime.now();
    }

    public void setWompiTransactionId(String wompiTransactionId) {
        this.wompiTransactionId = wompiTransactionId;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStatus(PaymentTransactionStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}
