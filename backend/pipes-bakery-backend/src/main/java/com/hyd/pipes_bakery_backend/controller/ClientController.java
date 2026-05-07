package com.hyd.pipes_bakery_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientResponseDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ApiError;
import com.hyd.pipes_bakery_backend.service.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clientes", description = "Gestion de clientes y consulta de sus pedidos.")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // GET /api/clients
    @GetMapping
    @Operation(summary = "Listar clientes", description = "Devuelve todos los clientes registrados.")
    @ApiResponse(responseCode = "200", description = "Listado de clientes",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClientResponseDTO.class))))
    public List<ClientResponseDTO> getAllClients() {
        return clientService.getAllClients();
    }

    // GET /api/clients/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", description = "Devuelve la informacion de un cliente concreto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ClientResponseDTO getClientById(@Parameter(description = "ID del cliente", example = "1") @NonNull @PathVariable Long id) {
        return clientService.getClientById(id);
    }

    // GET /api/clients/{id}/orders
    @GetMapping("/{id}/orders")
    @Operation(summary = "Listar pedidos de un cliente", description = "Devuelve los pedidos asociados a un cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos del cliente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public List<OrderResponseDTO> getOrdersByClient(@Parameter(description = "ID del cliente", example = "1") @NonNull @PathVariable Long id) {
        return clientService.getAllOrders(id);
    }

    // POST /api/clients
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear cliente", description = "Registra un nuevo cliente con su direccion principal.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado",
                    content = @Content(schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un recurso con los mismos datos unicos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ClientResponseDTO createClient(@Valid @RequestBody ClientRequestDTO client) {
        return clientService.createClient(client);
    }

    // UPDATE
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado",
                    content = @Content(schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un recurso con los mismos datos unicos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ClientResponseDTO updateClient(@Parameter(description = "ID del cliente", example = "1") @NonNull @PathVariable Long id, @Valid @RequestBody ClientRequestDTO updatedClient) {
        return clientService.updateClient(id, updatedClient);
    }

    // DELETE /api/clients/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public void deleteClient(@Parameter(description = "ID del cliente", example = "1") @NonNull @PathVariable Long id) {
        clientService.deleteClient(id);
    }
}
