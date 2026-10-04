package com.hyd.pipes_bakery_backend.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import java.time.LocalDate;
import com.hyd.pipes_bakery_backend.model.DeliverySlot;
import com.hyd.pipes_bakery_backend.exception.InvalidDeliveryDateException;
import com.hyd.pipes_bakery_backend.exception.DeliveryDateExpiredException;
import com.hyd.pipes_bakery_backend.dto.payment.RetryPaymentRequestDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.CheckoutSnapshot;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentStatusResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiTransactionDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.hyd.pipes_bakery_backend.exception.InvalidWebhookSignatureException;
import com.hyd.pipes_bakery_backend.exception.PaymentNotRetryableException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.OrderMapper;
import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.PaymentTransaction;
import com.hyd.pipes_bakery_backend.model.PaymentTransactionStatus;
import com.hyd.pipes_bakery_backend.repository.PaymentTransactionRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final UUID CART_ID = UUID.fromString("f4a9b6de-0c5d-4cb2-9a47-8dc413951f0f");

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private WompiClient wompiClient;

    @Mock
    private OrderService orderService;

    @Mock
    private CartStorage cartStorage;

    @Mock
    private OrderMapper orderMapper;

    private PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentTransactionRepository, wompiClient, orderService, cartStorage, objectMapper, orderMapper);
    }

    @Test
    void shouldStartCheckoutWithoutCreatingOrder() {
        CheckoutOrderRequestDTO request = buildCheckoutRequest();
        when(orderService.buildCheckoutSnapshot(CART_ID, request)).thenReturn(buildSnapshot(request));
        when(paymentTransactionRepository.existsByWompiReference(anyString())).thenReturn(false);
        when(wompiClient.buildCheckoutUrl(anyString(), eq(2900000L), eq("COP")))
                .thenReturn("https://checkout.wompi.co/p/?reference=MB-XXXX");

        PaymentSessionResponseDTO result = paymentService.startCheckout(CART_ID, request);

        assertThat(result.getCheckoutUrl()).isEqualTo("https://checkout.wompi.co/p/?reference=MB-XXXX");
        assertThat(result.getReference()).matches("MB-[A-Z0-9]{12}");

        ArgumentCaptor<PaymentTransaction> saved = ArgumentCaptor.forClass(PaymentTransaction.class);
        verify(paymentTransactionRepository).save(saved.capture());
        assertThat(saved.getValue().getOrder()).isNull();
        assertThat(saved.getValue().getCartId()).isEqualTo(CART_ID.toString());
        assertThat(saved.getValue().getAmountInCents()).isEqualTo(2900000L);
        assertThat(saved.getValue().getCheckoutData()).contains("felipe@melik.com");
        verify(orderService, never()).createPaidOrder(any());
        verify(cartStorage, never()).clearCart(any());
    }

    @Test
    void shouldRetryFailedPaymentWithSameCheckoutData() throws Exception {
        PaymentTransaction failed = buildTransaction("MB-FAILED00001");
        failed.setStatus(PaymentTransactionStatus.DECLINED);

        when(paymentTransactionRepository.findByWompiReference("MB-FAILED00001")).thenReturn(Optional.of(failed));
        when(paymentTransactionRepository.existsByWompiReference(anyString())).thenReturn(false);
        when(wompiClient.buildCheckoutUrl(anyString(), eq(2900000L), eq("COP")))
                .thenReturn("https://checkout.wompi.co/p/?reference=MB-NEW");

        PaymentSessionResponseDTO result = paymentService.retryPayment("MB-FAILED00001", null);

        assertThat(result.getReference()).isNotEqualTo("MB-FAILED00001");
        ArgumentCaptor<PaymentTransaction> saved = ArgumentCaptor.forClass(PaymentTransaction.class);
        verify(paymentTransactionRepository).save(saved.capture());
        assertThat(saved.getValue().getStatus()).isEqualTo(PaymentTransactionStatus.PENDING);
        assertThat(saved.getValue().getCartId()).isEqualTo(CART_ID.toString());
        assertThat(saved.getValue().getCheckoutData()).isEqualTo(failed.getCheckoutData());
    }

    @Test
    void shouldRefuseToRetryWithADeliveryDateThatNoLongerMeetsTheRule() throws Exception {
        PaymentTransaction failed = buildTransaction("MB-FAILED00001");
        failed.setStatus(PaymentTransactionStatus.DECLINED);
        when(paymentTransactionRepository.findByWompiReference("MB-FAILED00001")).thenReturn(Optional.of(failed));
        doThrow(new InvalidDeliveryDateException("Delivery date must be at least 4 days from today"))
                .when(orderService).validateDeliveryDate(any());

        assertThatThrownBy(() -> paymentService.retryPayment("MB-FAILED00001", null))
                .isInstanceOf(DeliveryDateExpiredException.class);
        verify(paymentTransactionRepository, never()).save(any());
        verify(wompiClient, never()).buildCheckoutUrl(anyString(), anyLong(), anyString());
    }

    @Test
    void shouldRetryWithANewDeliveryDateAndSaveItInTheNewAttempt() throws Exception {
        PaymentTransaction failed = buildTransaction("MB-FAILED00001");
        failed.setStatus(PaymentTransactionStatus.DECLINED);
        when(paymentTransactionRepository.findByWompiReference("MB-FAILED00001")).thenReturn(Optional.of(failed));
        when(paymentTransactionRepository.existsByWompiReference(anyString())).thenReturn(false);
        when(wompiClient.buildCheckoutUrl(anyString(), eq(2900000L), eq("COP")))
                .thenReturn("https://checkout.wompi.co/p/?reference=MB-NEW");

        paymentService.retryPayment("MB-FAILED00001",
                new RetryPaymentRequestDTO(LocalDate.of(2026, 10, 20), DeliverySlot.AFTERNOON));

        verify(orderService).validateDeliveryDate(LocalDate.of(2026, 10, 20));
        ArgumentCaptor<PaymentTransaction> saved = ArgumentCaptor.forClass(PaymentTransaction.class);
        verify(paymentTransactionRepository).save(saved.capture());
        CheckoutSnapshot snapshot = objectMapper.readValue(saved.getValue().getCheckoutData(), CheckoutSnapshot.class);
        assertThat(snapshot.getRequest().getDeliveryDate()).isEqualTo(LocalDate.of(2026, 10, 20));
        assertThat(snapshot.getRequest().getDeliverySlot()).isEqualTo(DeliverySlot.AFTERNOON);
        // The original failed attempt keeps its own data
        assertThat(objectMapper.readValue(failed.getCheckoutData(), CheckoutSnapshot.class).getRequest().getDeliveryDate())
                .isEqualTo(LocalDate.of(2026, 10, 10));
    }

    @Test
    void shouldRejectAnInvalidNewDeliveryDateOnRetry() throws Exception {
        PaymentTransaction failed = buildTransaction("MB-FAILED00001");
        failed.setStatus(PaymentTransactionStatus.DECLINED);
        when(paymentTransactionRepository.findByWompiReference("MB-FAILED00001")).thenReturn(Optional.of(failed));
        doThrow(new InvalidDeliveryDateException("Delivery date must be at least 4 days from today"))
                .when(orderService).validateDeliveryDate(LocalDate.of(2026, 10, 5));

        assertThatThrownBy(() -> paymentService.retryPayment("MB-FAILED00001",
                new RetryPaymentRequestDTO(LocalDate.of(2026, 10, 5), DeliverySlot.MORNING)))
                .isInstanceOf(InvalidDeliveryDateException.class);
        assertThatThrownBy(() -> paymentService.retryPayment("MB-FAILED00001",
                new RetryPaymentRequestDTO(LocalDate.of(2026, 10, 20), null)))
                .isInstanceOf(InvalidDeliveryDateException.class)
                .hasMessage("Both a delivery date and a time slot are required");
        verify(paymentTransactionRepository, never()).save(any());
    }

    @Test
    void shouldRejectRetryOfPendingPayment() throws Exception {
        PaymentTransaction pending = buildTransaction("MB-PENDING0001");
        when(paymentTransactionRepository.findByWompiReference("MB-PENDING0001")).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> paymentService.retryPayment("MB-PENDING0001", null))
                .isInstanceOf(PaymentNotRetryableException.class);
        verify(paymentTransactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenRetryingUnknownPayment() {
        when(paymentTransactionRepository.findByWompiReference("MB-MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.retryPayment("MB-MISSING", null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReportPendingStatusWithoutTransactionId() throws Exception {
        PaymentTransaction pending = buildTransaction("REF123");
        when(paymentTransactionRepository.findByWompiReferenceForUpdate("REF123")).thenReturn(Optional.of(pending));

        PaymentStatusResponseDTO result = paymentService.getPaymentStatus("REF123", null);

        assertThat(result.getStatus()).isEqualTo(PaymentStatusResponseDTO.Status.PENDING);
        verify(wompiClient, never()).fetchTransaction(anyString());
    }

    @Test
    void shouldReconcileApprovedStatusFromWompiAndCreateOrder() throws Exception {
        PaymentTransaction pending = buildTransaction("REF123");
        when(paymentTransactionRepository.findByWompiReferenceForUpdate("REF123")).thenReturn(Optional.of(pending));
        when(wompiClient.fetchTransaction("txn-1")).thenReturn(buildWompiTransaction("txn-1", "REF123", "APPROVED"));
        Order order = buildOrder("ABC123");
        OrderResponseDTO orderDto = org.mockito.Mockito.mock(OrderResponseDTO.class);
        when(orderService.createPaidOrder(any(CheckoutSnapshot.class))).thenReturn(order);
        when(paymentTransactionRepository.save(pending)).thenReturn(pending);
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        PaymentStatusResponseDTO result = paymentService.getPaymentStatus("REF123", "txn-1");

        assertThat(result.getStatus()).isEqualTo(PaymentStatusResponseDTO.Status.APPROVED);
        assertThat(result.getOrderId()).isEqualTo("ABC123");
        assertThat(result.getOrder()).isSameAs(orderDto);
        verify(cartStorage).clearCart(CART_ID);
    }

    @Test
    void shouldReconcileDeclinedStatusFromWompiWithoutCreatingOrder() throws Exception {
        PaymentTransaction pending = buildTransaction("REF123");
        when(paymentTransactionRepository.findByWompiReferenceForUpdate("REF123")).thenReturn(Optional.of(pending));
        when(wompiClient.fetchTransaction("txn-1")).thenReturn(buildWompiTransaction("txn-1", "REF123", "DECLINED"));
        when(paymentTransactionRepository.save(pending)).thenReturn(pending);

        PaymentStatusResponseDTO result = paymentService.getPaymentStatus("REF123", "txn-1");

        assertThat(result.getStatus()).isEqualTo(PaymentStatusResponseDTO.Status.FAILED);
        assertThat(result.getOrderId()).isNull();
        assertThat(result.getOrder()).isNull();
        verify(orderService, never()).createPaidOrder(any());
        verify(cartStorage, never()).clearCart(any());
    }

    @Test
    void shouldIgnoreWompiTransactionForADifferentReference() throws Exception {
        PaymentTransaction pending = buildTransaction("REF123");
        when(paymentTransactionRepository.findByWompiReferenceForUpdate("REF123")).thenReturn(Optional.of(pending));
        when(wompiClient.fetchTransaction("txn-other")).thenReturn(buildWompiTransaction("txn-other", "OTHER", "APPROVED"));

        PaymentStatusResponseDTO result = paymentService.getPaymentStatus("REF123", "txn-other");

        assertThat(result.getStatus()).isEqualTo(PaymentStatusResponseDTO.Status.PENDING);
        verify(orderService, never()).createPaidOrder(any());
    }

    @Test
    void shouldThrowWhenStatusRequestedForUnknownReference() {
        when(paymentTransactionRepository.findByWompiReferenceForUpdate("MB-MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentStatus("MB-MISSING", null))
                .isInstanceOf(ResourceNotFoundException.class);
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
        when(paymentTransactionRepository.findByWompiReferenceForUpdate("REF123")).thenReturn(Optional.empty());

        paymentService.handleWompiWebhook(event);

        verify(orderService, never()).createPaidOrder(any());
    }

    @Test
    void shouldCreatePaidOrderAndClearCartOnApprovedWebhook() throws Exception {
        PaymentTransaction transaction = buildTransaction("REF123");
        Order order = buildOrder("ABC123");

        WompiWebhookEventDTO event = buildWebhookEvent("transaction.updated", "REF123", "txn-1", "APPROVED");
        when(wompiClient.verifyEventChecksum(event)).thenReturn(true);
        when(paymentTransactionRepository.findByWompiReferenceForUpdate("REF123")).thenReturn(Optional.of(transaction));
        when(orderService.createPaidOrder(any(CheckoutSnapshot.class))).thenReturn(order);

        paymentService.handleWompiWebhook(event);

        ArgumentCaptor<CheckoutSnapshot> snapshot = ArgumentCaptor.forClass(CheckoutSnapshot.class);
        verify(orderService).createPaidOrder(snapshot.capture());
        assertThat(snapshot.getValue().getRequest().getClientEmail()).isEqualTo("felipe@melik.com");
        assertThat(snapshot.getValue().getTotalAmount()).isEqualByComparingTo(new BigDecimal("29000"));

        assertThat(transaction.getStatus()).isEqualTo(PaymentTransactionStatus.APPROVED);
        assertThat(transaction.getWompiTransactionId()).isEqualTo("txn-1");
        assertThat(transaction.getOrder()).isSameAs(order);
        verify(paymentTransactionRepository).save(transaction);
        verify(cartStorage).clearCart(CART_ID);
    }

    @Test
    void shouldNotCreateOrderOnDeclinedWebhook() throws Exception {
        PaymentTransaction transaction = buildTransaction("REF123");

        WompiWebhookEventDTO event = buildWebhookEvent("transaction.updated", "REF123", "txn-1", "DECLINED");
        when(wompiClient.verifyEventChecksum(event)).thenReturn(true);
        when(paymentTransactionRepository.findByWompiReferenceForUpdate("REF123")).thenReturn(Optional.of(transaction));

        paymentService.handleWompiWebhook(event);

        assertThat(transaction.getStatus()).isEqualTo(PaymentTransactionStatus.DECLINED);
        assertThat(transaction.getOrder()).isNull();
        verify(orderService, never()).createPaidOrder(any());
        verify(cartStorage, never()).clearCart(any());
    }

    @Test
    void shouldIgnoreDuplicateApprovedWebhook() throws Exception {
        PaymentTransaction transaction = buildTransaction("REF123");
        transaction.setWompiTransactionId("txn-1");
        transaction.setStatus(PaymentTransactionStatus.APPROVED);
        transaction.setOrder(buildOrder("ABC123"));

        WompiWebhookEventDTO event = buildWebhookEvent("transaction.updated", "REF123", "txn-1", "APPROVED");
        when(wompiClient.verifyEventChecksum(event)).thenReturn(true);
        when(paymentTransactionRepository.findByWompiReferenceForUpdate("REF123")).thenReturn(Optional.of(transaction));

        paymentService.handleWompiWebhook(event);

        verify(paymentTransactionRepository, never()).save(any());
        verify(orderService, never()).createPaidOrder(any());
    }

    private CheckoutOrderRequestDTO buildCheckoutRequest() {
        CheckoutOrderRequestDTO request = new CheckoutOrderRequestDTO();
        request.setClientFirstName("Felipe");
        request.setClientLastName("Hernandez");
        request.setClientEmail("felipe@melik.com");
        request.setClientPhoneNumber("3001234567");
        request.setReceiverName("Laura");
        request.setDeliveryDate(java.time.LocalDate.of(2026, 10, 10));
        request.setDeliverySlot(com.hyd.pipes_bakery_backend.model.DeliverySlot.MORNING);
        request.setShippingAddress(new AddressSnapshotDTO("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"));
        return request;
    }

    private CheckoutSnapshot buildSnapshot(CheckoutOrderRequestDTO request) {
        return new CheckoutSnapshot(
                request,
                List.of(new CheckoutSnapshot.Item(1L, 2, new BigDecimal("9500"))),
                new BigDecimal("10000")
        );
    }

    private PaymentTransaction buildTransaction(String reference) throws Exception {
        String checkoutData = objectMapper.writeValueAsString(buildSnapshot(buildCheckoutRequest()));
        return new PaymentTransaction(CART_ID.toString(), reference, 2900000L, checkoutData);
    }

    private WompiTransactionDTO buildWompiTransaction(String id, String reference, String status) {
        WompiTransactionDTO transaction = new WompiTransactionDTO();
        transaction.setId(id);
        transaction.setReference(reference);
        transaction.setStatus(status);
        return transaction;
    }

    private Order buildOrder(String publicId) {
        Order order = new Order(
                "Felipe", "Hernandez", "felipe@melik.com", "3001234567",
                new AddressSnapshot("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"),
                "Laura", new BigDecimal("10000")
        );
        order.setPublicId(publicId);
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
