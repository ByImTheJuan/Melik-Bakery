package com.hyd.pipes_bakery_backend.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.orderItem.OrderItemResponseDTO;
import com.hyd.pipes_bakery_backend.model.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pedido devuelto por la API.")
public class OrderResponseDTO {

    @Schema(description = "Identificador unico del pedido.", example = "ORD-20260507-0001")
    private String id;
    @Schema(description = "Nombre del cliente.", example = "Maria")
    private String clientFirstName;
    @Schema(description = "Apellidos del cliente.", example = "Garcia")
    private String clientLastName;
    @Schema(description = "Email de contacto del pedido.", example = "maria@example.com")
    private String clientEmail;
    @Schema(description = "Telefono de contacto del pedido.", example = "+34600111222")
    private String clientPhoneNumber;
    @Schema(description = "Lineas de producto incluidas en el pedido.")
    private List<OrderItemResponseDTO> items;
    @Schema(description = "Importe total del pedido.", example = "24.90")
    private BigDecimal totalAmount;
    @Schema(description = "Estado actual del pedido.", example = "CREATED")
    private OrderStatus status;
    @Schema(description = "Fecha y hora de creacion del pedido.", example = "2026-05-07T14:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Direccion de envio guardada para el pedido.")
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
