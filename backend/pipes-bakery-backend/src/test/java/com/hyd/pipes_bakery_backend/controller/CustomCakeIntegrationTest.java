package com.hyd.pipes_bakery_backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.hyd.pipes_bakery_backend.exception.UploadRateLimitException;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.service.UploadRateLimitService;
import com.hyd.pipes_bakery_backend.service.WompiClient;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class CustomCakeIntegrationTest {

    private static final String CAKE_BODY = """
            {
              "configuration": {
                "hasDietaryRestrictions": true,
                "dietaryRestrictions": "Sin gluten",
                "sizeId": "M",
                "flavourId": "chocolate",
                "decorativeTiers": 2,
                "colorId": "fresa",
                "text": "Feliz cumpleaños",
                "extraIds": ["velas"],
                "notes": "Letras en dorado"
              },
              "quantity": 1,
              "unitPrice": 1
            }
            """;

    // Delivery five days from now (Bogota time): always past the 4-day minimum
    private static final String CHECKOUT_BODY = """
            {
              "clientFirstName": "Felipe",
              "clientLastName": "Hernandez",
              "clientEmail": "felipe@melik.com",
              "clientPhoneNumber": "3001234567",
              "receiverName": "Laura",
              "deliveryDate": "%s",
              "deliverySlot": "AFTERNOON",
              "shippingAddress": {
                "street": "Calle 123",
                "additionalInformation": "Apto 1",
                "city": "Bogota",
                "zipCode": 110111,
                "country": "Colombia"
              }
            }
            """.formatted(java.time.LocalDate.now(java.time.ZoneId.of("America/Bogota")).plusDays(5));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private WompiClient wompiClient;

    @MockitoBean
    private CartStorage cartStorage;

    @MockitoBean
    private UploadRateLimitService uploadRateLimitService;

    private UUID cartId;

    @BeforeEach
    void setUp() {
        cartId = UUID.randomUUID();
        ShoppingCart cart = new ShoppingCart(cartId);
        // The same instance is mutated and "saved", which is enough to emulate Redis here
        when(cartStorage.getCart(cartId)).thenReturn(cart);

        when(wompiClient.buildCheckoutUrl(anyString(), anyLong(), anyString()))
                .thenReturn("https://checkout.wompi.co/p/?reference=MB-XXXX");
        when(wompiClient.verifyEventChecksum(any(WompiWebhookEventDTO.class))).thenReturn(true);
    }

    @Test
    void shouldExposeTheOptionsCatalog() throws Exception {
        mockMvc.perform(get("/api/custom-cakes/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sizes[0].id").value("S"))
                .andExpect(jsonPath("$.sizes.length()").value(3))
                .andExpect(jsonPath("$.sizes[2].tierDiameters[0]").value(20))
                .andExpect(jsonPath("$.sizes[2].tierDiameters[1]").value(15))
                .andExpect(jsonPath("$.maxDecorativeTiers").value(2))
                .andExpect(jsonPath("$.flavours[0].spongeColor").exists())
                .andExpect(jsonPath("$.colors[0].hex").exists())
                .andExpect(jsonPath("$.maxTextLength").value(40));
    }

    @Test
    void shouldAddACustomCakePricedByTheServer() throws Exception {
        // M 140000 + 2 decorative tiers 70000 + text 8000 + velas 5000; the client "unitPrice" is ignored
        mockMvc.perform(post("/api/cart/{cartId}/custom-cakes", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CAKE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].type").value("CUSTOM_CAKE"))
                .andExpect(jsonPath("$.items[0].productName").value("Torta personalizada"))
                .andExpect(jsonPath("$.items[0].unitPriceAtAdd").value(223000))
                .andExpect(jsonPath("$.items[0].lineId").isNotEmpty())
                .andExpect(jsonPath("$.items[0].customCake.flavourLabel").value("Chocolate"))
                .andExpect(jsonPath("$.items[0].customCake.extraLabels[0]").value("Velas"))
                .andExpect(jsonPath("$.items[0].customCake.dietaryRestrictions").value("Sin gluten"))
                .andExpect(jsonPath("$.items[0].customCake.notes").value("Letras en dorado"));
    }

    @Test
    void shouldUpdateAndRemoveACustomCakeLine() throws Exception {
        String lineId = addCake();

        mockMvc.perform(put("/api/cart/{cartId}/custom-cakes/{lineId}", cartId, lineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.itemsTotal").value(446000));

        mockMvc.perform(delete("/api/cart/{cartId}/custom-cakes/{lineId}", cartId, lineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void shouldRejectAnInvalidConfiguration() throws Exception {
        String body = CAKE_BODY.replace("\"dietaryRestrictions\": \"Sin gluten\"", "\"dietaryRestrictions\": \" \"");

        mockMvc.perform(post("/api/cart/{cartId}/custom-cakes", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please specify the dietary restrictions"));
    }

    @Test
    void shouldCreateAPaidOrderWithTheCakeDetailsOnceApproved() throws Exception {
        addCake();

        String session = mockMvc.perform(post("/api/cart/{cartId}/checkout", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CHECKOUT_BODY))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String reference = objectMapper.readTree(session).get("reference").asText();

        String payload = String.format(
                "{\"event\":\"transaction.updated\",\"timestamp\":1700000000," +
                "\"data\":{\"transaction\":{\"id\":\"txn-1\",\"reference\":\"%s\",\"status\":\"APPROVED\"}}," +
                "\"signature\":{\"properties\":[\"transaction.id\"],\"checksum\":\"whatever\"}}", reference);
        mockMvc.perform(post("/api/payments/webhook").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk());

        String statusResponse = mockMvc.perform(get("/api/payments/{reference}", reference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.order.items[0].type").value("CUSTOM_CAKE"))
                .andExpect(jsonPath("$.order.items[0].productName").value("Torta personalizada"))
                .andExpect(jsonPath("$.order.items[0].customCake.decorativeTiers").value(2))
                .andExpect(jsonPath("$.order.items[0].customCake.dietaryRestrictions").value("Sin gluten"))
                .andExpect(jsonPath("$.order.items[0].customCake.text").value("Feliz cumpleaños"))
                .andExpect(jsonPath("$.order.totalAmount").value(233000))
                .andReturn().getResponse().getContentAsString();

        String orderId = objectMapper.readTree(statusResponse).get("orderId").asText();
        Order order = orderRepository.findByPublicId(orderId).orElseThrow();
        OrderItem item = order.getItems().get(0);
        assertThat(item.getProduct()).isNull();
        assertThat(item.getCustomCakeDetails().getColorLabel()).isEqualTo("Rosa fresa");
        assertThat(item.getUnitPriceAtPurchase()).isEqualByComparingTo(new BigDecimal("223000"));
    }

    @Test
    void shouldRejectUploadsThatAreNotImages() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "notes.txt", "text/plain", "hola".getBytes());

        mockMvc.perform(multipart("/api/custom-cakes/images").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAnswerTooManyRequestsWhenTheUploadLimitIsReached() throws Exception {
        doThrow(new UploadRateLimitException("Too many image uploads. Try again later."))
                .when(uploadRateLimitService).assertUploadAllowed(anyString());
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});

        mockMvc.perform(multipart("/api/custom-cakes/images").file(file))
                .andExpect(status().isTooManyRequests());
    }

    private String addCake() throws Exception {
        String response = mockMvc.perform(post("/api/cart/{cartId}/custom-cakes", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CAKE_BODY))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("items").get(0).get("lineId").asText();
    }
}
