package com.hyd.pipes_bakery_backend.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.payment.PayableResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.hyd.pipes_bakery_backend.exception.OrderNotPayableException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.service.PaymentService;

@SuppressWarnings("null")
@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void shouldCreatePaymentSessionSuccessfully() throws Exception {
        when(paymentService.createPaymentSession("ABC123"))
                .thenReturn(new PaymentSessionResponseDTO("https://checkout.wompi.co/p/?reference=ABC123-XXXX", "ABC123-XXXX"));

        mockMvc.perform(post("/api/payments/orders/{orderId}/sessions", "ABC123"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.checkoutUrl").value("https://checkout.wompi.co/p/?reference=ABC123-XXXX"))
                .andExpect(jsonPath("$.reference").value("ABC123-XXXX"));

        verify(paymentService).createPaymentSession("ABC123");
    }

    @Test
    void shouldReturnNotFoundWhenCreatingSessionForMissingOrder() throws Exception {
        when(paymentService.createPaymentSession("MISSING"))
                .thenThrow(new ResourceNotFoundException("Order not found with id MISSING"));

        mockMvc.perform(post("/api/payments/orders/{orderId}/sessions", "MISSING"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflictWhenOrderIsNotPayable() throws Exception {
        when(paymentService.createPaymentSession("ABC123"))
                .thenThrow(new OrderNotPayableException("Order ABC123 is not payable in its current status"));

        mockMvc.perform(post("/api/payments/orders/{orderId}/sessions", "ABC123"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnPayableTrue() throws Exception {
        when(paymentService.isOrderPayable("ABC123")).thenReturn(new PayableResponseDTO(true));

        mockMvc.perform(get("/api/payments/orders/{orderId}/payable", "ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payable").value(true));
    }

    @Test
    void shouldReturnPayableFalseForUnknownOrderWithout404() throws Exception {
        when(paymentService.isOrderPayable("MISSING")).thenReturn(new PayableResponseDTO(false));

        mockMvc.perform(get("/api/payments/orders/{orderId}/payable", "MISSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payable").value(false));
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
