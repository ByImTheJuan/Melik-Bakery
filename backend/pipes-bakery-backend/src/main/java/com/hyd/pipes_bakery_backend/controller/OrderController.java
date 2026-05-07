package com.hyd.pipes_bakery_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderStatusUpdateRequestDTO;
import com.hyd.pipes_bakery_backend.exception.ApiError;
import com.hyd.pipes_bakery_backend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import static com.hyd.pipes_bakery_backend.config.OpenApiConfig.SECURITY_SCHEME_NAME;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Pedidos", description = "Consulta y administracion de pedidos.")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //GET /api/orders
    @GetMapping()
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @Operation(summary = "Listar pedidos", description = "Devuelve todos los pedidos. Requiere JWT de administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de pedidos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponseDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Token ausente o no valido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos de administrador",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    //GET /api/orders/{orderId}
    @GetMapping("/{orderId}")
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @Operation(summary = "Obtener pedido por ID", description = "Devuelve un pedido concreto. Requiere JWT de administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o no valido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos de administrador",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public OrderResponseDTO getOrderById(@Parameter(description = "ID del pedido", example = "ORD-20260507-0001") @NonNull @PathVariable String orderId) {
        return orderService.getOrderById(orderId);
    }

    //PATCH /api/orders/{orderId}/status
    @PatchMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @Operation(summary = "Actualizar estado de pedido", description = "Cambia el estado de un pedido. Requiere JWT de administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o no valido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos de administrador",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public OrderResponseDTO updateOrderStatus(
            @Parameter(description = "ID del pedido", example = "ORD-20260507-0001") @NonNull @PathVariable String orderId,
            @Valid @RequestBody OrderStatusUpdateRequestDTO request) {

        return orderService.updateOrderStatus(orderId, request.getStatus());
    }
}
