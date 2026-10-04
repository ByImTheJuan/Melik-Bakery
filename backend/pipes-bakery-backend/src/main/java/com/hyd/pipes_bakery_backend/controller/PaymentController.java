package com.hyd.pipes_bakery_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.payment.PaymentSessionResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.RetryPaymentRequestDTO;
import com.hyd.pipes_bakery_backend.dto.payment.PaymentStatusResponseDTO;
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

    //POST /api/payments/{reference}/retry
    @PostMapping("/{reference}/retry")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Reintentar pago", description = "Crea un nuevo intento de pago en Wompi con los mismos datos de un intento fallido.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nueva sesion de pago creada",
                    content = @Content(schema = @Schema(implementation = PaymentSessionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Intento de pago no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "400", description = "La nueva fecha de entrega no es valida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "El intento de pago no ha fallado y no puede reintentarse",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "La fecha de entrega original ya no cumple el plazo minimo: hay que elegir otra",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public PaymentSessionResponseDTO retry(
            @Parameter(description = "Referencia del intento de pago", example = "MB-AB12CD34EF56") @NonNull @PathVariable String reference,
            @RequestBody(required = false) RetryPaymentRequestDTO newDelivery) {
        return paymentService.retryPayment(reference, newDelivery);
    }

    //GET /api/payments/{reference}
    @GetMapping("/{reference}")
    @Operation(summary = "Consultar resultado de un pago", description = "Devuelve si el intento de pago esta pendiente, aprobado (con el ID del pedido creado) o fallido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del intento de pago",
                    content = @Content(schema = @Schema(implementation = PaymentStatusResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Intento de pago no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public PaymentStatusResponseDTO status(
            @Parameter(description = "Referencia del intento de pago", example = "MB-AB12CD34EF56") @NonNull @PathVariable String reference,
            @Parameter(description = "ID de la transaccion que Wompi anade a la URL de redireccion") @RequestParam(required = false) String transactionId) {
        return paymentService.getPaymentStatus(reference, transactionId);
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
