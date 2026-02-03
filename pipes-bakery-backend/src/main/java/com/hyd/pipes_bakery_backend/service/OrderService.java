package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.dto.orderItem.OrderItemResponseDTO;
import com.hyd.pipes_bakery_backend.exception.CartIsEmptyException;
import com.hyd.pipes_bakery_backend.exception.InvalidAddressException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.Client;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.ClientRepository;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@Service
public class OrderService implements IOrderService{

    private final OrderRepository orderRepository;

    private final ClientRepository clientRepository;

    private final ProductRepository productRepository;

    private final CartStorage cartStorage;

    public OrderService(OrderRepository orderRepository, ClientRepository clientRepository, ProductRepository productRepository, CartStorage cartStorage) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.cartStorage = cartStorage;
    }

    @Override
    public List<OrderResponseDTO> getAllOrders(){
        return orderRepository.findAll()
                    .stream()
                    .map(this::toDto)
                    .toList();
    }

    @Override
    public OrderResponseDTO getOrderById(Long orderId) {
        
        return toDto(orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException(
                "Order not found with id " + orderId
        )));
    }

    @Override
    public List<OrderResponseDTO> getOrdersByClient(Long clientId) {
        
        return orderRepository.findAll().stream()
            .filter(order -> order.getClient().getId().equals(clientId))
            .map(this::toDto)
            .toList();
    }

    //Cancelling an order just changes its state
    @Override
    public OrderResponseDTO cancelOrder(Long orderId) {
        
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status) {
        
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                })
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @Override
    public OrderResponseDTO checkout(Long clientId, CheckoutOrderRequestDTO request){

        validateShippingAddress(request.getShippingAddress());

        ShoppingCart cart = cartStorage.getCart(clientId);
        if(cart.isEmpty())
            throw new CartIsEmptyException("Cart is empty. Impossible to do checkout");
        else {

            Client client = clientRepository.findById(clientId).orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + clientId));
            Order order = new Order(client, toAddressEntity(request.getShippingAddress()));
            
            //Transform CartItems into OrderItems
            List<OrderItem> items = cart.getItems().stream()
                    .map(itemDto -> {

                        Product product = productRepository.findById(itemDto.getProductId())
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "Product not found with id " + itemDto.getProductId()
                                        )
                                );

                        return new OrderItem(
                                product,
                                itemDto.getQuantity()
                        );
                    })
                    .toList();

            order.setItems(items);
            orderRepository.save(order);

            cartStorage.clearCart(clientId);

            return toDto(order);
        }
        
    }

    private OrderResponseDTO toDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO(order.getId(), 
                                                        order.getClient().getId(),
                                                        toItemsDtoList(order.getItems()), 
                                                        order.getTotalAmount(), 
                                                        order.getStatus(),
                                                        order.getCreatedAt(),
                                                        toAddressDto(order.getAddress()));
        return dto;
    }

    private OrderItemResponseDTO toItemDto(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPriceAtPurchase()
        );
    }

    private List<OrderItemResponseDTO> toItemsDtoList(List<OrderItem> items) {
        return items.stream()
            .map(this::toItemDto)
            .toList();
    }

    private AddressSnapshotDTO toAddressDto(AddressSnapshot address) {
        return new AddressSnapshotDTO(address.getStreet(),
                                        address.getAdditionalInformation(),
                                        address.getCity(),
                                        address.getZipCode(),
                                        address.getCountry());
    }

    private AddressSnapshot toAddressEntity(AddressSnapshotDTO dto){
        return new AddressSnapshot(dto.getStreet(),
        dto.getAdditionalInformation(),
        dto.getCity(),
        dto.getZipCode(),
        dto.getCountry());
    }

    private void validateShippingAddress(AddressSnapshotDTO address) {
    if (!"Bogotá".equalsIgnoreCase(address.getCity())) {
        throw new InvalidAddressException("We only ship to Bogotá DC");
    }

    if (!"Colombia".equalsIgnoreCase(address.getCountry())) {
        throw new InvalidAddressException("Invalid country");
    }
}
}
