package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.payment.PayableResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.hyd.pipes_bakery_backend.exception.InvalidWebhookSignatureException;
import com.hyd.pipes_bakery_backend.exception.OrderNotPayableException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.PaymentTransaction;
import com.hyd.pipes_bakery_backend.model.PaymentTransactionStatus;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.PaymentTransactionRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private WompiClient wompiClient;

    @Mock
    private OrderService orderService;

    private PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(orderRepository, paymentTransactionRepository, wompiClient, orderService);
    }

    @Test
    void shouldCreatePaymentSessionForPendingOrder() {
        Order order = buildOrder("ABC123", OrderStatus.PAYMENT_PENDING, new BigDecimal("29000"));

        when(orderRepository.findByPublicId("ABC123")).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.existsByWompiReference(anyString())).thenReturn(false);
        when(wompiClient.buildCheckoutUrl(anyString(), eq(2900000L), eq("COP"), eq("ABC123")))
                .thenReturn("https://checkout.wompi.co/p/?reference=ABC123-XXXX");

        PaymentSessionResponseDTO result = paymentService.createPaymentSession("ABC123");

        assertThat(result.getCheckoutUrl()).isEqualTo("https://checkout.wompi.co/p/?reference=ABC123-XXXX");
        assertThat(result.getReference()).startsWith("ABC123-");
        verify(paymentTransactionRepository).save(any(PaymentTransaction.class));
    }

    @Test
    void shouldThrowWhenOrderNotFoundForPaymentSession() {
        when(orderRepository.findByPublicId("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.createPaymentSession("MISSING"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldRejectPaymentSessionForNonPendingOrder() {
        Order order = buildOrder("ABC123", OrderStatus.PAID, new BigDecimal("29000"));
        when(orderRepository.findByPublicId("ABC123")).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.createPaymentSession("ABC123"))
                .isInstanceOf(OrderNotPayableException.class);
    }

    @Test
    void shouldReportPayableTrueForPendingOrder() {
        Order order = buildOrder("ABC123", OrderStatus.PAYMENT_PENDING, new BigDecimal("29000"));
        when(orderRepository.findByPublicId("ABC123")).thenReturn(Optional.of(order));

        PayableResponseDTO result = paymentService.isOrderPayable("ABC123");

        assertThat(result.isPayable()).isTrue();
    }

    @Test
    void shouldReportPayableFalseForPaidOrder() {
        Order order = buildOrder("ABC123", OrderStatus.PAID, new BigDecimal("29000"));
        when(orderRepository.findByPublicId("ABC123")).thenReturn(Optional.of(order));

        PayableResponseDTO result = paymentService.isOrderPayable("ABC123");

        assertThat(result.isPayable()).isFalse();
    }

    @Test
    void shouldReportPayableFalseForUnknownOrder() {
        when(orderRepository.findByPublicId("MISSING")).thenReturn(Optional.empty());

        PayableResponseDTO result = paymentService.isOrderPayable("MISSING");

        assertThat(result.isPayable()).isFalse();
    }

    @Test
    void shouldRejectWebhookWithInvalidChecksum() throws Exception {
        WompiWebhookEventDTO event = buildWebhookEvent("transaction.updated", "REF123", "txn-1", "APPROVED");
        when(wompiClient.verifyEventChecksum(event)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.handleWompiWebhook(event))
                .isInstanceOf(InvalidWebhookSignatureException.class);

        verify(paymentTransactionRepository, never()).save(any());
    }

    @Test
    void shouldIgnoreNonTransactionUpdatedEvents() throws Exception {
        WompiWebhookEventDTO event = buildWebhookEvent("transaction.created", "REF123", "txn-1", "APPROVED");

        paymentService.handleWompiWebhook(event);

        verify(wompiClient, never()).verifyEventChecksum(any());
    }

    @Test
    void shouldIgnoreWebhookForUnknownReference() throws Exception {
        WompiWebhookEventDTO event = buildWebhookEvent("transaction.updated", "REF123", "txn-1", "APPROVED");
        when(wompiClient.verifyEventChecksum(event)).thenReturn(true);
        when(paymentTransactionRepository.findByWompiReference("REF123")).thenReturn(Optional.empty());

        paymentService.handleWompiWebhook(event);

        verify(orderService, never()).markOrderAsPaid(anyString());
    }

    @Test
    void shouldMarkOrderPaidOnApprovedWebhook() throws Exception {
        Order order = buildOrder("ABC123", OrderStatus.PAYMENT_PENDING, new BigDecimal("29000"));
        PaymentTransaction transaction = new PaymentTransaction(order, "REF123", 2900000L);

        WompiWebhookEventDTO event = buildWebhookEvent("transaction.updated", "REF123", "txn-1", "APPROVED");
        when(wompiClient.verifyEventChecksum(event)).thenReturn(true);
        when(paymentTransactionRepository.findByWompiReference("REF123")).thenReturn(Optional.of(transaction));

        paymentService.handleWompiWebhook(event);

        assertThat(transaction.getStatus()).isEqualTo(PaymentTransactionStatus.APPROVED);
        assertThat(transaction.getWompiTransactionId()).isEqualTo("txn-1");
        verify(paymentTransactionRepository).save(transaction);
        verify(orderService).markOrderAsPaid("ABC123");
    }

    @Test
    void shouldNotTouchOrderOnDeclinedWebhook() throws Exception {
        Order order = buildOrder("ABC123", OrderStatus.PAYMENT_PENDING, new BigDecimal("29000"));
        PaymentTransaction transaction = new PaymentTransaction(order, "REF123", 2900000L);

        WompiWebhookEventDTO event = buildWebhookEvent("transaction.updated", "REF123", "txn-1", "DECLINED");
        when(wompiClient.verifyEventChecksum(event)).thenReturn(true);
        when(paymentTransactionRepository.findByWompiReference("REF123")).thenReturn(Optional.of(transaction));

        paymentService.handleWompiWebhook(event);

        assertThat(transaction.getStatus()).isEqualTo(PaymentTransactionStatus.DECLINED);
        verify(orderService, never()).markOrderAsPaid(anyString());
    }

    @Test
    void shouldDedupeDuplicateApprovedWebhook() throws Exception {
        Order order = buildOrder("ABC123", OrderStatus.PAID, new BigDecimal("29000"));
        PaymentTransaction transaction = new PaymentTransaction(order, "REF123", 2900000L);
        transaction.setWompiTransactionId("txn-1");
        transaction.setStatus(PaymentTransactionStatus.APPROVED);

        WompiWebhookEventDTO event = buildWebhookEvent("transaction.updated", "REF123", "txn-1", "APPROVED");
        when(wompiClient.verifyEventChecksum(event)).thenReturn(true);
        when(paymentTransactionRepository.findByWompiReference("REF123")).thenReturn(Optional.of(transaction));

        paymentService.handleWompiWebhook(event);

        verify(paymentTransactionRepository, never()).save(any());
        verify(orderService, never()).markOrderAsPaid(anyString());
    }

    private Order buildOrder(String publicId, OrderStatus status, BigDecimal totalAmount) {
        Order order = new Order(
                "Felipe", "Hernandez", "felipe@melik.com", "3001234567",
                new AddressSnapshot("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"),
                "Laura", new BigDecimal("10000")
        );
        order.setPublicId(publicId);
        order.setStatus(status);
        ReflectionTestUtils.setField(order, "totalAmount", totalAmount);
        return order;
    }

    private WompiWebhookEventDTO buildWebhookEvent(String eventName, String reference, String transactionId, String status) throws Exception {
        String json = String.format(
                "{\"transaction\":{\"id\":\"%s\",\"reference\":\"%s\",\"status\":\"%s\"}}",
                transactionId, reference, status);

        WompiWebhookEventDTO event = new WompiWebhookEventDTO();
        event.setEvent(eventName);
        event.setData(objectMapper.readTree(json));
        event.setTimestamp(1700000000L);

        WompiWebhookEventDTO.Signature signature = new WompiWebhookEventDTO.Signature();
        signature.setProperties(java.util.List.of("transaction.id", "transaction.status"));
        signature.setChecksum("irrelevant-for-mocked-verification");
        event.setSignature(signature);

        return event;
    }
}
