package com.hyd.pipes_bakery_backend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;

@Component
public class OrderAutoCancelScheduler {

    private static final Logger log = LoggerFactory.getLogger(OrderAutoCancelScheduler.class);
    private static final Duration PENDING_TIMEOUT = Duration.ofHours(1);

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderAutoCancelScheduler(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void cancelExpiredPendingOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minus(PENDING_TIMEOUT);

        for (Order order : orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PAYMENT_PENDING, cutoff)) {
            log.info("Auto-cancelling order {} after {} unpaid", order.getPublicId(), PENDING_TIMEOUT);
            orderService.cancelOrder(order.getPublicId());
        }
    }
}
