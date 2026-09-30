package com.hyd.pipes_bakery_backend.service;

import com.hyd.pipes_bakery_backend.dto.payment.WompiTransactionDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;

public interface WompiClient {

    String buildIntegritySignature(String reference, long amountInCents, String currency);

    String buildCheckoutUrl(String reference, long amountInCents, String currency, String orderId);

    boolean verifyEventChecksum(WompiWebhookEventDTO event);

    WompiTransactionDTO fetchTransaction(String wompiTransactionId);
}
