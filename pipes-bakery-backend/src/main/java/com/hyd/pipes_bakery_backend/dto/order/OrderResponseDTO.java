package com.hyd.pipes_bakery_backend.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.orderItem.OrderItemResponseDTO;
import com.hyd.pipes_bakery_backend.model.OrderStatus;

public class OrderResponseDTO {

    private Long id;
    private Long clientId;
    private List<OrderItemResponseDTO> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private AddressSnapshotDTO shippingAddress;

    public OrderResponseDTO(Long id, Long clientId, List<OrderItemResponseDTO> items, BigDecimal totalAmount,
            OrderStatus status, LocalDateTime createdAt, AddressSnapshotDTO shippingAddress) {
        this.id = id;
        this.clientId = clientId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.shippingAddress = shippingAddress;
    }

    public Long getId() {
        return id;
    }
    public Long getClientId() {
        return clientId;
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
    public void setId(Long id) {
        this.id = id;
    }
    public void setClientId(Long clientId) {
        this.clientId = clientId;
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
