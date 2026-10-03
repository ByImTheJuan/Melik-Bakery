package com.hyd.pipes_bakery_backend.service;

import java.util.UUID;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentStatusResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;

public interface IPaymentService {

    PaymentSessionResponseDTO startCheckout(UUID cartId, CheckoutOrderRequestDTO request);

    PaymentSessionResponseDTO retryPayment(@NonNull String reference);

    PaymentStatusResponseDTO getPaymentStatus(@NonNull String reference, String wompiTransactionId);

    void handleWompiWebhook(WompiWebhookEventDTO event);
}
