package com.hyd.pipes_bakery_backend.mapper;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.dto.orderItem.OrderItemResponseDTO;
import com.hyd.pipes_bakery_backend.model.OrderItem;

@Component
public class OrderItemMapper {


    public OrderItemResponseDTO toDto(OrderItem orderItem) {
        return new OrderItemResponseDTO(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPriceAtPurchase()
        );
    }

    public List<OrderItemResponseDTO> toDtoList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toDto)
                .toList();
    }
}
