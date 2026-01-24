package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientResponseDTO;
import com.hyd.pipes_bakery_backend.model.Client;

public interface IClientService {

    List<ClientResponseDTO> getAllClients();

    ClientResponseDTO getClientById(@NonNull Long id);

    ClientResponseDTO createClient(ClientRequestDTO Client);

    void deleteClient(@NonNull Long id);

    ClientResponseDTO updateClient(@NonNull Long id, ClientRequestDTO updatedClient);

    ClientResponseDTO toDto(Client Client);

    Client toEntity(ClientRequestDTO dto);
}
