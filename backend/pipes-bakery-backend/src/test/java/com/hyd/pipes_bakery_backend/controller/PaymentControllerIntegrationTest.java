package com.hyd.pipes_bakery_backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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
import com.hyd.pipes_bakery_backend.model.CartItem;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.PaymentTransaction;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.PaymentTransactionRepository;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;
import com.hyd.pipes_bakery_backend.service.WompiClient;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class PaymentControllerIntegrationTest {

    private static final String CHECKOUT_BODY = """
            {
              "clientFirstName": "Felipe",
              "clientLastName": "Hernandez",
              "clientEmail": "felipe@melik.com",
              "clientPhoneNumber": "3001234567",
              "receiverName": "Laura",
              "shippingAddress": {
                "street": "Calle 123",
                "additionalInformation": "Apto 1",
                "city": "Bogota",
                "zipCode": 110111,
                "country": "Colombia"
              }
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @MockitoBean
    private WompiClient wompiClient;

    @MockitoBean
    private CartStorage cartStorage;

    private UUID cartId;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setName("Croissant");
        product.setPrice(new BigDecimal("9500"));
        product.setDescription("Mantequilla");
        product.setIngredients(List.of("Harina"));
        product.setImageUrl("croissant.jpg");
        Product savedProduct = productRepository.save(product);

        cartId = UUID.randomUUID();
        ShoppingCart cart = new ShoppingCart(cartId);
        cart.setItems(List.of(new CartItem(savedProduct.getId(), "Croissant", 2, new BigDecimal("9500"), "croissant.jpg")));
        when(cartStorage.getCart(cartId)).thenReturn(cart);

        when(wompiClient.buildCheckoutUrl(anyString(), anyLong(), anyString()))
                .thenReturn("https://checkout.wompi.co/p/?reference=MB-XXXX");
        when(wompiClient.verifyEventChecksum(any(WompiWebhookEventDTO.class))).thenReturn(true);
    }

    @Test
    void shouldCreateOrderOnlyWhenPaymentIsApproved() throws Exception {
        long ordersBefore = orderRepository.count();

        String reference = startCheckout();

        assertThat(orderRepository.count()).isEqualTo(ordersBefore);
        PaymentTransaction transaction = paymentTransactionRepository.findByWompiReference(reference).orElseThrow();
        assertThat(transaction.getOrder()).isNull();
        assertThat(transaction.getAmountInCents()).isEqualTo(2900000L);

        mockMvc.perform(get("/api/payments/{reference}", reference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.order").doesNotExist());

        sendWebhook(reference, "txn-1", "APPROVED");

        assertThat(orderRepository.count()).isEqualTo(ordersBefore + 1);
        verify(cartStorage).clearCart(cartId);

        String statusResponse = mockMvc.perform(get("/api/payments/{reference}", reference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.order.clientFirstName").value("Felipe"))
                .andExpect(jsonPath("$.order.clientEmail").value("felipe@melik.com"))
                .andExpect(jsonPath("$.order.items[0].productName").value("Croissant"))
                .andExpect(jsonPath("$.order.items[0].quantity").value(2))
                .andExpect(jsonPath("$.order.totalAmount").value(29000))
                .andReturn().getResponse().getContentAsString();

        String orderId = objectMapper.readTree(statusResponse).get("orderId").asText();
        Order order = orderRepository.findByPublicId(orderId).orElseThrow();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("29000"));

        // A repeated webhook must not create a second order
        sendWebhook(reference, "txn-1", "APPROVED");
        assertThat(orderRepository.count()).isEqualTo(ordersBefore + 1);
    }

    @Test
    void shouldNotCreateOrderWhenPaymentFailsAndAllowRetry() throws Exception {
        long ordersBefore = orderRepository.count();

        String reference = startCheckout();
        sendWebhook(reference, "txn-1", "DECLINED");

        assertThat(orderRepository.count()).isEqualTo(ordersBefore);
        verify(cartStorage, never()).clearCart(any());

        mockMvc.perform(get("/api/payments/{reference}", reference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));

        String retryResponse = mockMvc.perform(post("/api/payments/{reference}/retry", reference))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String newReference = objectMapper.readTree(retryResponse).get("reference").asText();
        assertThat(newReference).isNotEqualTo(reference);
        assertThat(paymentTransactionRepository.findByWompiReference(newReference)).isPresent();
    }

    @Test
    void shouldRejectRetryOfPendingPayment() throws Exception {
        String reference = startCheckout();

        mockMvc.perform(post("/api/payments/{reference}/retry", reference))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundForUnknownPaymentReference() throws Exception {
        mockMvc.perform(get("/api/payments/{reference}", "MB-NOPE"))
                .andExpect(status().isNotFound());
    }

    private String startCheckout() throws Exception {
        String response = mockMvc.perform(post("/api/cart/{cartId}/checkout", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CHECKOUT_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.checkoutUrl").value("https://checkout.wompi.co/p/?reference=MB-XXXX"))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("reference").asText();
    }

    private void sendWebhook(String reference, String transactionId, String status) throws Exception {
        String payload = String.format(
                "{\"event\":\"transaction.updated\",\"timestamp\":1700000000," +
                "\"data\":{\"transaction\":{\"id\":\"%s\",\"reference\":\"%s\",\"status\":\"%s\"}}," +
                "\"signature\":{\"properties\":[\"transaction.id\"],\"checksum\":\"whatever\"}}",
                transactionId, reference, status);

        mockMvc.perform(post("/api/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
    }
}
