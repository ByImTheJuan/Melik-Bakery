package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.payment.CheckoutSnapshot;
import com.hyd.pipes_bakery_backend.exception.InvalidDeliveryDateException;
import com.hyd.pipes_bakery_backend.exception.DeliveryDateExpiredException;
import com.hyd.pipes_bakery_backend.dto.payment.RetryPaymentRequestDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentStatusResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiTransactionDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.hyd.pipes_bakery_backend.exception.InvalidWebhookSignatureException;
import com.hyd.pipes_bakery_backend.exception.PaymentNotRetryableException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.OrderMapper;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.PaymentTransaction;
import com.hyd.pipes_bakery_backend.model.PaymentTransactionStatus;
import com.hyd.pipes_bakery_backend.repository.PaymentTransactionRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@Service
public class PaymentService implements IPaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final String CURRENCY = "COP";
    private static final String REFERENCE_PREFIX = "MB-";
    private static final String REFERENCE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int REFERENCE_SUFFIX_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Set<PaymentTransactionStatus> FAILED_STATUSES = EnumSet.of(
            PaymentTransactionStatus.DECLINED,
            PaymentTransactionStatus.VOIDED,
            PaymentTransactionStatus.ERROR
    );

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final WompiClient wompiClient;
    private final OrderService orderService;
    private final CartStorage cartStorage;
    private final ObjectMapper objectMapper;
    private final OrderMapper orderMapper;

    public PaymentService(
            PaymentTransactionRepository paymentTransactionRepository,
            WompiClient wompiClient,
            OrderService orderService,
            CartStorage cartStorage,
            ObjectMapper objectMapper,
            OrderMapper orderMapper
    ) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.wompiClient = wompiClient;
        this.orderService = orderService;
        this.cartStorage = cartStorage;
        this.objectMapper = objectMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public PaymentSessionResponseDTO startCheckout(UUID cartId, CheckoutOrderRequestDTO request) {
        CheckoutSnapshot snapshot = orderService.buildCheckoutSnapshot(cartId, request);
        return createAttempt(cartId.toString(), snapshot, toJson(snapshot));
    }

    @Override
    @Transactional
    public PaymentSessionResponseDTO retryPayment(@NonNull String reference, RetryPaymentRequestDTO newDelivery) {
        PaymentTransaction previous = findByReference(reference);

        if (!FAILED_STATUSES.contains(previous.getStatus())) {
            throw new PaymentNotRetryableException("Payment " + reference + " cannot be retried in its current status");
        }

        CheckoutSnapshot snapshot = fromJson(previous.getCheckoutData());
        CheckoutOrderRequestDTO request = snapshot.getRequest();

        if (newDelivery != null && (newDelivery.getDeliveryDate() != null || newDelivery.getDeliverySlot() != null)) {
            if (newDelivery.getDeliveryDate() == null || newDelivery.getDeliverySlot() == null) {
                throw new InvalidDeliveryDateException("Both a delivery date and a time slot are required");
            }
            orderService.validateDeliveryDate(newDelivery.getDeliveryDate());
            request.setDeliveryDate(newDelivery.getDeliveryDate());
            request.setDeliverySlot(newDelivery.getDeliverySlot());
        } else {
            // Days may have passed since checkout: the preparation-time rule applies to every new attempt
            boolean stillValid;
            try {
                orderService.validateDeliveryDate(request.getDeliveryDate());
                stillValid = request.getDeliverySlot() != null;
            } catch (InvalidDeliveryDateException ex) {
                stillValid = false;
            }
            if (!stillValid) {
                throw new DeliveryDateExpiredException(
                        "The delivery date is no longer available. Please choose a new one");
            }
        }

        return createAttempt(previous.getCartId(), snapshot, toJson(snapshot));
    }

    @Override
    @Transactional
    public PaymentStatusResponseDTO getPaymentStatus(@NonNull String reference, String wompiTransactionId) {
        // Locked from the first read so a webhook processed at the same time cannot leave this copy stale
        PaymentTransaction transaction = paymentTransactionRepository.findByWompiReferenceForUpdate(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with reference " + reference));

        // The customer can come back from Wompi before the webhook arrives; ask Wompi directly in that case
        if (transaction.getStatus() == PaymentTransactionStatus.PENDING && wompiTransactionId != null) {
            WompiTransactionDTO wompiTransaction = wompiClient.fetchTransaction(wompiTransactionId);

            if (wompiTransaction != null && reference.equals(wompiTransaction.getReference())) {
                transaction = applyTransactionStatus(reference, wompiTransaction.getStatus(), wompiTransaction.getId());
            }
        }

        return toStatusResponse(transaction);
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

        applyTransactionStatus(reference, rawStatus, wompiTransactionId);
    }

    /**
     * Records Wompi's status for an attempt and, the first time it is APPROVED, creates the
     * paid order from the stored snapshot and empties the cart. Safe to call repeatedly.
     */
    private PaymentTransaction applyTransactionStatus(String reference, String rawStatus, String wompiTransactionId) {
        PaymentTransaction transaction = paymentTransactionRepository.findByWompiReferenceForUpdate(reference)
                .orElse(null);

        if (transaction == null) {
            log.warn("Ignoring Wompi status for unknown reference {}", reference);
            return null;
        }

        if (transaction.getOrder() != null) {
            return transaction; // already approved and processed, idempotent no-op
        }

        PaymentTransactionStatus newStatus;
        try {
            newStatus = PaymentTransactionStatus.valueOf(rawStatus);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Ignoring Wompi status {} for reference {}", rawStatus, reference);
            return transaction;
        }

        transaction.setWompiTransactionId(wompiTransactionId);
        transaction.setStatus(newStatus);

        if (newStatus == PaymentTransactionStatus.APPROVED) {
            Order order = orderService.createPaidOrder(fromJson(transaction.getCheckoutData()));
            transaction.setOrder(order);
            cartStorage.clearCart(UUID.fromString(transaction.getCartId()));
            log.info("Payment {} approved, created order {}", reference, order.getPublicId());
        }

        return paymentTransactionRepository.save(transaction);
    }

    private PaymentSessionResponseDTO createAttempt(String cartId, CheckoutSnapshot snapshot, String checkoutData) {
        String reference = generateUniqueReference();
        long amountInCents = snapshot.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValueExact();

        paymentTransactionRepository.save(new PaymentTransaction(cartId, reference, amountInCents, checkoutData));

        String checkoutUrl = wompiClient.buildCheckoutUrl(reference, amountInCents, CURRENCY);
        return new PaymentSessionResponseDTO(checkoutUrl, reference);
    }

    private PaymentStatusResponseDTO toStatusResponse(PaymentTransaction transaction) {
        if (transaction.getOrder() != null) {
            Order order = transaction.getOrder();
            return new PaymentStatusResponseDTO(PaymentStatusResponseDTO.Status.APPROVED, order.getPublicId(), orderMapper.toDto(order));
        }
        if (FAILED_STATUSES.contains(transaction.getStatus())) {
            return new PaymentStatusResponseDTO(PaymentStatusResponseDTO.Status.FAILED, null);
        }
        return new PaymentStatusResponseDTO(PaymentStatusResponseDTO.Status.PENDING, null);
    }

    private PaymentTransaction findByReference(String reference) {
        return paymentTransactionRepository.findByWompiReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with reference " + reference));
    }

    private String toJson(CheckoutSnapshot snapshot) {
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialise checkout snapshot", e);
        }
    }

    private CheckoutSnapshot fromJson(String checkoutData) {
        if (checkoutData == null) {
            throw new PaymentNotRetryableException("Payment has no checkout data to build an order from");
        }
        try {
            return objectMapper.readValue(checkoutData, CheckoutSnapshot.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not read checkout snapshot", e);
        }
    }

    private String generateUniqueReference() {
        String reference;
        do {
            reference = REFERENCE_PREFIX + generateReferenceSuffix();
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
