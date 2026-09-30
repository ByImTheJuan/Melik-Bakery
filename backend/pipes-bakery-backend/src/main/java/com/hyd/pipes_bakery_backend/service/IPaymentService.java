package com.hyd.pipes_bakery_backend.service;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.dto.payment.PayableResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;

public interface IPaymentService {

    PaymentSessionResponseDTO createPaymentSession(@NonNull String orderId);

    PayableResponseDTO isOrderPayable(@NonNull String orderId);

    void handleWompiWebhook(WompiWebhookEventDTO event);
}
