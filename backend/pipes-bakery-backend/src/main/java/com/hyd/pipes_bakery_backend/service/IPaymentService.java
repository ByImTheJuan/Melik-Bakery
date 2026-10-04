package com.hyd.pipes_bakery_backend.service;

import java.util.UUID;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.RetryPaymentRequestDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentStatusResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;

public interface IPaymentService {

    PaymentSessionResponseDTO startCheckout(UUID cartId, CheckoutOrderRequestDTO request);

    /**
     * Starts a new payment attempt for a failed one. The original delivery date is kept unless a new one
     * is given; if the original no longer meets the minimum preparation time, a new one is required.
     *
     * @throws com.hyd.pipes_bakery_backend.exception.DeliveryDateExpiredException if a new date is needed
     */
    PaymentSessionResponseDTO retryPayment(@NonNull String reference, RetryPaymentRequestDTO newDelivery);

    PaymentStatusResponseDTO getPaymentStatus(@NonNull String reference, String wompiTransactionId);

    void handleWompiWebhook(WompiWebhookEventDTO event);
}
