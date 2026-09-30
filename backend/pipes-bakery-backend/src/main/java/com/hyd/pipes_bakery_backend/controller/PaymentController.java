package com.hyd.pipes_bakery_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.payment.PayableResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.WompiWebhookEventDTO;
import com.hyd.pipes_bakery_backend.exception.ApiError;
import com.hyd.pipes_bakery_backend.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Pagos", description = "Inicio y confirmacion de pagos via Wompi.")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    //POST /api/payments/orders/{orderId}/sessions
    @PostMapping("/orders/{orderId}/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Iniciar sesion de pago", description = "Crea un nuevo intento de pago en Wompi para un pedido pendiente de pago.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sesion de pago creada",
                    content = @Content(schema = @Schema(implementation = PaymentSessionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "El pedido ya no admite pagos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public PaymentSessionResponseDTO createSession(
            @Parameter(description = "ID publico del pedido", example = "AB12CD") @NonNull @PathVariable String orderId) {
        return paymentService.createPaymentSession(orderId);
    }

    //GET /api/payments/orders/{orderId}/payable
    @GetMapping("/orders/{orderId}/payable")
    @Operation(summary = "Consultar si un pedido admite pago", description = "Devuelve unicamente si el pedido sigue pendiente de pago, sin exponer ningun otro dato.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado de la consulta",
                    content = @Content(schema = @Schema(implementation = PayableResponseDTO.class)))
    })
    public PayableResponseDTO isPayable(
            @Parameter(description = "ID publico del pedido", example = "AB12CD") @NonNull @PathVariable String orderId) {
        return paymentService.isOrderPayable(orderId);
    }

    //POST /api/payments/webhook
    @PostMapping("/webhook")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Webhook de eventos de Wompi", description = "Recibe y procesa los eventos de transacciones enviados por Wompi.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento procesado"),
            @ApiResponse(responseCode = "400", description = "Firma del evento no valida",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public void webhook(@RequestBody WompiWebhookEventDTO event) {
        paymentService.handleWompiWebhook(event);
    }
}
