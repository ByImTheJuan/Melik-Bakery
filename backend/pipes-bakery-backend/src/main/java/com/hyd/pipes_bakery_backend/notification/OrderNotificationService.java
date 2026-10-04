package com.hyd.pipes_bakery_backend.notification;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import com.hyd.pipes_bakery_backend.email.EmailMessage;
import com.hyd.pipes_bakery_backend.email.EmailSender;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;

/** Builds and sends the customer email for an order status. Knows nothing about the email provider. */
@Service
public class OrderNotificationService {

    static final String TEMPLATE = "email/order-status";

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationService.class);

    private final OrderRepository orderRepository;
    private final EmailSender emailSender;
    private final ITemplateEngine templateEngine;

    public OrderNotificationService(OrderRepository orderRepository, EmailSender emailSender, ITemplateEngine templateEngine) {
        this.orderRepository = orderRepository;
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @Transactional(readOnly = true)
    public void sendStatusEmail(String orderPublicId, OrderStatus status) {
        Optional<OrderEmailType> type = OrderEmailType.forStatus(status);
        if (type.isEmpty()) {
            return;
        }

        Order order = orderRepository.findByPublicId(orderPublicId).orElse(null);
        if (order == null) {
            log.warn("Not sending {} email: order {} not found", status, orderPublicId);
            return;
        }

        OrderEmailView view = OrderEmailView.from(order);
        emailSender.send(new EmailMessage(
                order.getClientEmail(),
                type.get().subject(order.getPublicId()),
                renderHtml(type.get(), view),
                renderText(type.get(), view),
                "order-" + order.getPublicId() + "-" + status.name()
        ));
        log.info("Sent {} email for order {}", status, orderPublicId);
    }

    private String renderHtml(OrderEmailType type, OrderEmailView view) {
        Context context = new Context();
        context.setVariable("type", type);
        context.setVariable("order", view);
        return templateEngine.process(TEMPLATE, context);
    }

    /** Plain-text alternative for clients that don't show HTML (and for the log sender). */
    private String renderText(OrderEmailType type, OrderEmailView view) {
        StringBuilder text = new StringBuilder();
        text.append("Hola ").append(view.getCustomerFirstName()).append(",\n\n")
                .append(type.getHeadline()).append('\n')
                .append(type.getMessage()).append("\n\n")
                .append("Pedido #").append(view.getPublicId()).append('\n');

        for (OrderEmailView.Line line : view.getLines()) {
            text.append("- ").append(line.getQuantity()).append(" x ").append(line.getName())
                    .append(": ").append(line.getLineTotal()).append('\n');
            for (String detail : line.getDetails()) {
                text.append("    ").append(detail).append('\n');
            }
        }

        text.append("Envío: ").append(view.getShippingCost()).append('\n')
                .append("Total: ").append(view.getTotal()).append("\n\n");

        if (view.getDeliveryDate() != null) {
            text.append("Entrega: ").append(view.getDeliveryDate());
            if (view.getDeliverySlot() != null) {
                text.append(" (").append(view.getDeliverySlot()).append(')');
            }
            text.append('\n');
        }
        if (view.getReceiverName() != null) {
            text.append("Recibe: ").append(view.getReceiverName()).append('\n');
        }
        if (view.getAddress() != null) {
            text.append("Dirección: ").append(view.getAddress()).append('\n');
        }

        return text.append("\nMelik Bakery").toString();
    }
}
