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
import com.hyd.pipes_bakery_backend.service.ClientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // GET /api/clients
    @GetMapping
    public List<ClientResponseDTO> getAllClients() {
        return clientService.getAllClients();
    }

    // GET /api/clients/{id}
    @GetMapping("/{id}")
    public ClientResponseDTO getClientById(@NonNull @PathVariable Long id) {
        return clientService.getClientById(id);
    }

    // GET /api/clients/{id}/orders
    @GetMapping("/{id}/orders")
    public List<OrderResponseDTO> getOrdersByClient(@NonNull @PathVariable Long id) {
        return clientService.getAllOrders(id);
    }

    // POST /api/clients
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponseDTO createClient(@Valid @RequestBody ClientRequestDTO client) {
        return clientService.createClient(client);
    }

    // UPDATE
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientResponseDTO updateClient(@NonNull @PathVariable Long id, @Valid @RequestBody ClientRequestDTO updatedClient) {
        return clientService.updateClient(id, updatedClient);
    }

    // DELETE /api/clients/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@NonNull @PathVariable Long id) {
        clientService.deleteClient(id);
    }
}
