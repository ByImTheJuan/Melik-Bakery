package com.hyd.pipes_bakery_backend.mapper;

import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.model.Order;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final AddressMapper addressMapper;

    public OrderMapper(OrderItemMapper orderItemMapper, AddressMapper addressMapper) {
        this.orderItemMapper = orderItemMapper;
        this.addressMapper = addressMapper;
    }

    public OrderResponseDTO toDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO(order.getId(),
                                                        order.getClientFirstName(), 
                                                        order.getClientLastName(),
                                                        order.getClientEmail(),
                                                        order.getClientPhoneNumber(), 
                                                        orderItemMapper.toDtoList(order.getItems()), 
                                                        order.getTotalAmount(), 
                                                        order.getStatus(),
                                                        order.getCreatedAt(),
                                                        addressMapper.toSnapshotDto(order.getAddress()));
        return dto;
    }
}
