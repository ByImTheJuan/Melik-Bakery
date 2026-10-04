package com.hyd.pipes_bakery_backend.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;
import com.hyd.pipes_bakery_backend.email.EmailMessage;
import com.hyd.pipes_bakery_backend.email.EmailSender;
import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.DeliverySlot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class OrderNotificationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EmailSender emailSender;

    private OrderNotificationService notificationService;

    @BeforeEach
    void setUp() {
        // Same resolution Spring Boot configures: classpath:/templates/<name>.html
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        notificationService = new OrderNotificationService(orderRepository, emailSender, templateEngine);
    }

    @ParameterizedTest
    @CsvSource({
            "PAID,      Recibimos tu pedido #ABC123,             ¡Recibimos tu pedido!",
            "PREPARING, Tu pedido #ABC123 está en preparación,   Tu pedido está en preparación",
            "SHIPPED,   Tu pedido #ABC123 va en camino,          ¡Tu pedido va en camino!",
            "DELIVERED, Tu pedido #ABC123 fue entregado,         Tu pedido fue entregado",
            "CANCELLED, Tu pedido #ABC123 fue cancelado,         Tu pedido fue cancelado"
    })
    void shouldEmailTheCustomerForEachStatus(OrderStatus status, String subject, String headline) {
        when(orderRepository.findByPublicId("ABC123")).thenReturn(Optional.of(buildOrder(status)));

        notificationService.sendStatusEmail("ABC123", status);

        EmailMessage message = captureSentEmail();
        assertThat(message.to()).isEqualTo("felipe@melik.com");
        assertThat(message.subject()).isEqualTo(subject);
        assertThat(message.idempotencyKey()).isEqualTo("order-ABC123-" + status.name());
        assertThat(message.inlineImages()).singleElement().satisfies(logo -> {
            assertThat(logo.contentId()).isEqualTo("melik-logo");
            assertThat(logo.contentType()).isEqualTo("image/jpeg");
            assertThat(logo.content()).isNotEmpty();
        });
        assertThat(message.html())
                .contains(headline)
                .contains("src=\"cid:melik-logo\"")
                .contains("Hola <span>Felipe</span>")
                .contains("#ABC123")
                .contains("2 × Croissant")
                .contains("$19.000")
                .contains("$10.000")
                .contains("$29.000")
                .contains("viernes 9 de octubre de 2026")
                .contains("(Tarde)")
                .contains("Calle 123, Apto 1, Bogota");
        assertThat(message.text())
                .contains(headline)
                .contains("2 x Croissant: $19.000")
                .contains("Total: $29.000");
    }

    @Test
    void shouldDescribePersonalizedCakesInTheEmail() {
        Order order = buildOrder(OrderStatus.PAID);
        CustomCakeDetails cake = new CustomCakeDetails();
        cake.setSizeLabel("Mediana");
        cake.setServings(12);
        cake.setFlavourLabel("Chocolate");
        cake.setColorLabel("Rosa");
        cake.setText("Feliz cumple <Ana>");
        cake.setExtraLabels(List.of("Fresas", "Velas"));
        order.setItems(new ArrayList<>(List.of(OrderItem.customCake("Torta personalizada", 1, new BigDecimal("150000"), cake))));
        when(orderRepository.findByPublicId("ABC123")).thenReturn(Optional.of(order));

        notificationService.sendStatusEmail("ABC123", OrderStatus.PAID);

        EmailMessage message = captureSentEmail();
        assertThat(message.html())
                .contains("1 × Torta personalizada")
                .contains("Tamaño: Mediana (12 personas)")
                .contains("Sabor: Chocolate")
                .contains("Extras: Fresas, Velas")
                // Customer text is escaped, never injected as HTML
                .contains("Feliz cumple &lt;Ana&gt;")
                .contains("$160.000");
    }

    @Test
    void shouldSkipTheDeliveryLineForOrdersWithoutADeliveryDate() {
        Order order = buildOrder(OrderStatus.PREPARING);
        order.setDeliveryDate(null);
        order.setDeliverySlot(null);
        when(orderRepository.findByPublicId("ABC123")).thenReturn(Optional.of(order));

        notificationService.sendStatusEmail("ABC123", OrderStatus.PREPARING);

        assertThat(captureSentEmail().html()).doesNotContain("Entrega:");
    }

    @Test
    void shouldNotEmailForPendingPayments() {
        notificationService.sendStatusEmail("ABC123", OrderStatus.PAYMENT_PENDING);

        verify(orderRepository, never()).findByPublicId(any());
        verify(emailSender, never()).send(any());
    }

    @Test
    void shouldNotEmailWhenTheOrderDoesNotExist() {
        when(orderRepository.findByPublicId("MISSING")).thenReturn(Optional.empty());

        notificationService.sendStatusEmail("MISSING", OrderStatus.PAID);

        verify(emailSender, never()).send(any());
    }

    private EmailMessage captureSentEmail() {
        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(captor.capture());
        return captor.getValue();
    }

    private Order buildOrder(OrderStatus status) {
        Product product = new Product();
        product.setId(1L);
        product.setName("Croissant");
        product.setPrice(new BigDecimal("9500"));

        OrderItem item = new OrderItem(product, 2);
        item.setItemName("Croissant");

        Order order = new Order(
                "Felipe",
                "Hernandez",
                "felipe@melik.com",
                "3001234567",
                new AddressSnapshot("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"),
                "Laura",
                new BigDecimal("10000")
        );
        order.setPublicId("ABC123");
        order.setItems(new ArrayList<>(List.of(item)));
        order.setStatus(status);
        order.setDeliveryDate(LocalDate.of(2026, 10, 9));
        order.setDeliverySlot(DeliverySlot.AFTERNOON);
        return order;
    }
}
