package com.hyd.pipes_bakery_backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.PaymentTransactionRepository;
import com.hyd.pipes_bakery_backend.service.WompiClient;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @MockitoBean
    private WompiClient wompiClient;

    @Test
    void shouldCreateSessionAndConfirmPaymentViaWebhook() throws Exception {
        Order order = saveOrder("AB12CD", OrderStatus.PAYMENT_PENDING);

        mockMvc.perform(get("/api/payments/orders/{orderId}/payable", "AB12CD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payable").value(true));

        when(wompiClient.buildCheckoutUrl(anyString(), anyLong(), anyString(), anyString()))
                .thenReturn("https://checkout.wompi.co/p/?reference=AB12CD-XXXX");

        String sessionResponse = mockMvc.perform(post("/api/payments/orders/{orderId}/sessions", "AB12CD"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.checkoutUrl").value("https://checkout.wompi.co/p/?reference=AB12CD-XXXX"))
                .andReturn().getResponse().getContentAsString();

        String reference = objectMapper.readTree(sessionResponse).get("reference").asText();
        assertThat(paymentTransactionRepository.findByWompiReference(reference)).isPresent();

        when(wompiClient.verifyEventChecksum(any(WompiWebhookEventDTO.class))).thenReturn(true);

        String webhookPayload = String.format(
                "{\"event\":\"transaction.updated\",\"timestamp\":1700000000," +
                "\"data\":{\"transaction\":{\"id\":\"txn-1\",\"reference\":\"%s\",\"status\":\"APPROVED\"}}," +
                "\"signature\":{\"properties\":[\"transaction.id\"],\"checksum\":\"whatever\"}}",
                reference);

        mockMvc.perform(post("/api/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webhookPayload))
                .andExpect(status().isOk());

        assertThat(orderRepository.findByPublicId("AB12CD").orElseThrow().getStatus()).isEqualTo(OrderStatus.PAID);

        mockMvc.perform(get("/api/payments/orders/{orderId}/payable", "AB12CD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payable").value(false));
    }

    @Test
    void shouldRejectSessionCreationForNonPendingOrder() throws Exception {
        saveOrder("XY99ZZ", OrderStatus.PAID);

        mockMvc.perform(post("/api/payments/orders/{orderId}/sessions", "XY99ZZ"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnPayableFalseForUnknownOrderIdWithoutLeakingNotFound() throws Exception {
        mockMvc.perform(get("/api/payments/orders/{orderId}/payable", "NOPE00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payable").value(false));
    }

    private Order saveOrder(String publicId, OrderStatus status) {
        Order order = new Order(
                "Felipe", "Hernandez", "felipe@melik.com", "3001234567",
                new AddressSnapshot("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"),
                "Laura", new BigDecimal("10000")
        );
        order.setPublicId(publicId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
