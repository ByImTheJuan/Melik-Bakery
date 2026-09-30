package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.hyd.pipes_bakery_backend.dto.payment.PayableResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.hyd.pipes_bakery_backend.exception.InvalidWebhookSignatureException;
import com.hyd.pipes_bakery_backend.exception.OrderNotPayableException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.PaymentTransaction;
import com.hyd.pipes_bakery_backend.model.PaymentTransactionStatus;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.PaymentTransactionRepository;

@Service
public class PaymentService implements IPaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final String CURRENCY = "COP";
    private static final String REFERENCE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int REFERENCE_SUFFIX_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final WompiClient wompiClient;
    private final OrderService orderService;

    public PaymentService(
            OrderRepository orderRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            WompiClient wompiClient,
            OrderService orderService
    ) {
        this.orderRepository = orderRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.wompiClient = wompiClient;
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public PaymentSessionResponseDTO createPaymentSession(@NonNull String orderId) {
        Order order = orderRepository.findByPublicId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));

        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new OrderNotPayableException("Order " + orderId + " is not payable in its current status");
        }

        String reference = generateUniqueReference(order.getPublicId());
        long amountInCents = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValueExact();

        PaymentTransaction transaction = new PaymentTransaction(order, reference, amountInCents);
        paymentTransactionRepository.save(transaction);

        String checkoutUrl = wompiClient.buildCheckoutUrl(reference, amountInCents, CURRENCY, order.getPublicId());
        return new PaymentSessionResponseDTO(checkoutUrl, reference);
    }

    @Override
    public PayableResponseDTO isOrderPayable(@NonNull String orderId) {
        boolean payable = orderRepository.findByPublicId(orderId)
                .map(order -> order.getStatus() == OrderStatus.PAYMENT_PENDING)
                .orElse(false);
        return new PayableResponseDTO(payable);
    }

    @Override
    @Transactional
    public void handleWompiWebhook(WompiWebhookEventDTO event) {
        if (!"transaction.updated".equals(event.getEvent())) {
            return;
        }

        if (!wompiClient.verifyEventChecksum(event)) {
            throw new InvalidWebhookSignatureException("Invalid Wompi webhook signature");
        }

        JsonNode transactionNode = event.getData().path("transaction");
        String reference = transactionNode.path("reference").asText(null);
        String wompiTransactionId = transactionNode.path("id").asText(null);
        String rawStatus = transactionNode.path("status").asText(null);

        if (reference == null || rawStatus == null) {
            log.warn("Ignoring Wompi webhook with missing reference/status");
            return;
        }

        PaymentTransaction transaction = paymentTransactionRepository.findByWompiReference(reference)
                .orElse(null);

        if (transaction == null) {
            log.warn("Ignoring Wompi webhook for unknown reference {}", reference);
            return;
        }

        if (transaction.getStatus() == PaymentTransactionStatus.APPROVED
                && wompiTransactionId != null
                && wompiTransactionId.equals(transaction.getWompiTransactionId())) {
            return; // already processed, idempotent no-op
        }

        PaymentTransactionStatus newStatus;
        try {
            newStatus = PaymentTransactionStatus.valueOf(rawStatus);
        } catch (IllegalArgumentException e) {
            log.warn("Ignoring Wompi webhook with unrecognized status {}", rawStatus);
            return;
        }

        transaction.setWompiTransactionId(wompiTransactionId);
        transaction.setStatus(newStatus);
        paymentTransactionRepository.save(transaction);

        if (newStatus == PaymentTransactionStatus.APPROVED) {
            orderService.markOrderAsPaid(transaction.getOrder().getPublicId());
        }
    }

    private String generateUniqueReference(String orderPublicId) {
        String reference;
        do {
            reference = orderPublicId + "-" + generateReferenceSuffix();
        } while (paymentTransactionRepository.existsByWompiReference(reference));
        return reference;
    }

    private String generateReferenceSuffix() {
        StringBuilder suffix = new StringBuilder(REFERENCE_SUFFIX_LENGTH);
        for (int i = 0; i < REFERENCE_SUFFIX_LENGTH; i++) {
            suffix.append(REFERENCE_CHARS.charAt(RANDOM.nextInt(REFERENCE_CHARS.length())));
        }
        return suffix.toString();
    }
}
