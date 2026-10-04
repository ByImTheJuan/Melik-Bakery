package com.hyd.pipes_bakery_backend.mapper;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.dto.orderItem.OrderItemResponseDTO;
import com.hyd.pipes_bakery_backend.model.CartItemType;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.Product;

@Component
public class OrderItemMapper {


    public OrderItemResponseDTO toDto(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        // Custom cakes have no product; product lines from before V7 have no stored name
        String name = orderItem.getItemName() != null || product == null
                ? orderItem.getItemName()
                : product.getName();

        OrderItemResponseDTO dto = new OrderItemResponseDTO(
                orderItem.getId(),
                product != null ? product.getId() : 0L,
                name,
                orderItem.getQuantity(),
                orderItem.getUnitPriceAtPurchase()
        );
        dto.setType(orderItem.isCustomCake() ? CartItemType.CUSTOM_CAKE : CartItemType.PRODUCT);
        dto.setCustomCake(orderItem.getCustomCakeDetails());
        return dto;
    }

    public List<OrderItemResponseDTO> toDtoList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toDto)
                .toList();
    }
}
