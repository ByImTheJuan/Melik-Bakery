package com.hyd.pipes_bakery_backend.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.orderItem.OrderItemResponseDTO;
import com.hyd.pipes_bakery_backend.model.OrderStatus;


public class OrderResponseDTO {

    private String id;
    private String clientFirstName;
    private String clientLastName;
    private String clientEmail;
    private String clientPhoneNumber;
    private List<OrderItemResponseDTO> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private AddressSnapshotDTO shippingAddress;

    public OrderResponseDTO(String id, String clientFirstName, String clientLastName, String clientEmail, String clientPhoneNumber,
            List<OrderItemResponseDTO> items, BigDecimal totalAmount,
            OrderStatus status, LocalDateTime createdAt, AddressSnapshotDTO shippingAddress) {
        this.id = id;
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.clientEmail = clientEmail;
        this.clientPhoneNumber = clientPhoneNumber;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.shippingAddress = shippingAddress;
    }

    public String getId() {
        return id;
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
    public List<OrderItemResponseDTO> getItems() {
        return items;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public AddressSnapshotDTO getShippingAddress() {
        return shippingAddress;
    }
    public void setId(String id) {
        this.id = id;
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
    public void setItems(List<OrderItemResponseDTO> items) {
        this.items = items;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setShippingAddress(AddressSnapshotDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
