package com.hyd.pipes_bakery_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderAutoCancelSchedulerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    private OrderAutoCancelScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new OrderAutoCancelScheduler(orderRepository, orderService);
    }

    @Test
    void shouldCancelOnlyExpiredPendingOrders() {
        Order expiredOrder = buildOrder("OLD001");

        when(orderRepository.findByStatusAndCreatedAtBefore(eq(OrderStatus.PAYMENT_PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(expiredOrder));

        scheduler.cancelExpiredPendingOrders();

        verify(orderService).cancelOrder("OLD001");
    }

    @Test
    void shouldNotCancelAnythingWhenNoOrdersExpired() {
        when(orderRepository.findByStatusAndCreatedAtBefore(eq(OrderStatus.PAYMENT_PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of());

        scheduler.cancelExpiredPendingOrders();

        verify(orderService, never()).cancelOrder(org.mockito.ArgumentMatchers.anyString());
    }

    private Order buildOrder(String publicId) {
        Order order = new Order(
                "Felipe", "Hernandez", "felipe@melik.com", "3001234567",
                new AddressSnapshot("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"),
                "Laura"
        );
        order.setPublicId(publicId);
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }
}
