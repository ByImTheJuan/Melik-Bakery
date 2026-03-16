package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.exception.CartIsEmptyException;
import com.hyd.pipes_bakery_backend.exception.InvalidAddressException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.AddressMapper;
import com.hyd.pipes_bakery_backend.mapper.OrderMapper;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@Service
public class OrderService implements IOrderService{

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final CartStorage cartStorage;

    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, CartStorage cartStorage,
        OrderMapper orderMapper, AddressMapper addressMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartStorage = cartStorage;
        this.orderMapper = orderMapper;
        this.addressMapper = addressMapper;
    }

    @Override
    public List<OrderResponseDTO> getAllOrders(){
        return orderRepository.findAll()
                    .stream()
                    .map(orderMapper::toDto)
                    .toList();
    }

    @Override
    public OrderResponseDTO getOrderById(@NonNull Long orderId) {
        
        return orderMapper.toDto(orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException(
                "Order not found with id " + orderId
        )));
    }

    //Cancelling an order just changes its state
    @Override
    public OrderResponseDTO cancelOrder(@NonNull Long orderId) {
        
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(@NonNull Long orderId, OrderStatus status) {
        
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                })
                .map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @Transactional
    @Override
    public OrderResponseDTO checkout(Long cartId, CheckoutOrderRequestDTO request){

        validateShippingAddress(request.getShippingAddress());

        ShoppingCart cart = cartStorage.getCart(cartId);
        if(cart.isEmpty())
            throw new CartIsEmptyException("Cart is empty. Impossible to checkout");
        else {

            //Client's Address is null
            Order order = new Order(request.getClientFirstName(), request.getClientLastName(),
            request.getClientEmail(), request.getClientPhoneNumber(),
            addressMapper.toSnapshotEntity(request.getShippingAddress()), request.getReceiverName());
            
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

            cartStorage.clearCart(cartId);

            return orderMapper.toDto(order);
        }
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
