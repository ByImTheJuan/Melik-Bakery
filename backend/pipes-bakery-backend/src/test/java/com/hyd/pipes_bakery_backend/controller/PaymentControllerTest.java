package com.hyd.pipes_bakery_backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.ArgumentCaptor;
import com.hyd.pipes_bakery_backend.model.DeliverySlot;
import com.hyd.pipes_bakery_backend.exception.DeliveryDateExpiredException;
import com.hyd.pipes_bakery_backend.dto.payment.RetryPaymentRequestDTO;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentStatusResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.hyd.pipes_bakery_backend.exception.PaymentNotRetryableException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.service.PaymentService;

@SuppressWarnings("null")
@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void shouldRetryFailedPayment() throws Exception {
        when(paymentService.retryPayment(eq("MB-OLD"), any()))
                .thenReturn(new PaymentSessionResponseDTO("https://checkout.wompi.co/p/?reference=MB-NEW", "MB-NEW"));

        mockMvc.perform(post("/api/payments/{reference}/retry", "MB-OLD"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.checkoutUrl").value("https://checkout.wompi.co/p/?reference=MB-NEW"))
                .andExpect(jsonPath("$.reference").value("MB-NEW"));

        verify(paymentService).retryPayment(eq("MB-OLD"), any());
    }

    @Test
    void shouldReturnNotFoundWhenRetryingUnknownPayment() throws Exception {
        when(paymentService.retryPayment(eq("MISSING"), any()))
                .thenThrow(new ResourceNotFoundException("Payment not found with reference MISSING"));

        mockMvc.perform(post("/api/payments/{reference}/retry", "MISSING"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflictWhenPaymentIsNotRetryable() throws Exception {
        when(paymentService.retryPayment(eq("MB-PENDING"), any()))
                .thenThrow(new PaymentNotRetryableException("Payment MB-PENDING cannot be retried in its current status"));

        mockMvc.perform(post("/api/payments/{reference}/retry", "MB-PENDING"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldAskForANewDateWhenTheOriginalOneHasExpired() throws Exception {
        when(paymentService.retryPayment(eq("MB-OLD"), any()))
                .thenThrow(new DeliveryDateExpiredException("The delivery date is no longer available. Please choose a new one"));

        mockMvc.perform(post("/api/payments/{reference}/retry", "MB-OLD"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("The delivery date is no longer available. Please choose a new one"));
    }

    @Test
    void shouldPassTheNewDeliveryDateToTheRetry() throws Exception {
        when(paymentService.retryPayment(eq("MB-OLD"), any()))
                .thenReturn(new PaymentSessionResponseDTO("https://checkout.wompi.co/p/?reference=MB-NEW", "MB-NEW"));

        mockMvc.perform(post("/api/payments/{reference}/retry", "MB-OLD")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"deliveryDate\": \"2026-10-12\", \"deliverySlot\": \"MORNING\"}"))
                .andExpect(status().isCreated());

        ArgumentCaptor<RetryPaymentRequestDTO> body = ArgumentCaptor.forClass(RetryPaymentRequestDTO.class);
        verify(paymentService).retryPayment(eq("MB-OLD"), body.capture());
        assertThat(body.getValue().getDeliveryDate()).isEqualTo(java.time.LocalDate.of(2026, 10, 12));
        assertThat(body.getValue().getDeliverySlot()).isEqualTo(DeliverySlot.MORNING);
    }

    @Test
    void shouldReturnPaymentStatusPassingWompiTransactionId() throws Exception {
        when(paymentService.getPaymentStatus("MB-REF", "txn-1"))
                .thenReturn(new PaymentStatusResponseDTO(PaymentStatusResponseDTO.Status.APPROVED, "ABC123"));

        mockMvc.perform(get("/api/payments/{reference}", "MB-REF").param("transactionId", "txn-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.orderId").value("ABC123"));
    }

    @Test
    void shouldReturnPaymentStatusWithoutTransactionId() throws Exception {
        when(paymentService.getPaymentStatus("MB-REF", null))
                .thenReturn(new PaymentStatusResponseDTO(PaymentStatusResponseDTO.Status.FAILED, null));

        mockMvc.perform(get("/api/payments/{reference}", "MB-REF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void shouldAcceptWompiWebhook() throws Exception {
        mockMvc.perform(post("/api/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"event\":\"transaction.updated\"}"))
                .andExpect(status().isOk());

        verify(paymentService).handleWompiWebhook(org.mockito.ArgumentMatchers.any(WompiWebhookEventDTO.class));
    }
}
