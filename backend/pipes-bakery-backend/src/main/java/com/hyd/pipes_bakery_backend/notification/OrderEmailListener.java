package com.hyd.pipes_bakery_backend.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEmailListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEmailListener.class);

    private final OrderNotificationService notificationService;

    public OrderEmailListener(OrderNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Runs only once the order change is committed (never for a rolled-back payment) and on another
     * thread, so a slow or failing email provider can't affect payments or the admin panel.
     * fallbackExecution: admin status updates don't run inside a transaction.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        try {
            notificationService.sendStatusEmail(event.orderPublicId(), event.status());
        } catch (RuntimeException e) {
            log.error("Could not send {} email for order {}", event.status(), event.orderPublicId(), e);
        }
    }
}
