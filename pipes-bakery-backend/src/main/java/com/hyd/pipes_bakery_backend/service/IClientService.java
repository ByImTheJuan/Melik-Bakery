package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientResponseDTO;
import com.hyd.pipes_bakery_backend.model.Client;

public interface IClientService {

    List<ClientResponseDTO> getAllClients();

    ClientResponseDTO getClientById(Long id);

    ClientResponseDTO createClient(ClientRequestDTO Client);

    void deleteClient(Long id);

    ClientResponseDTO updateClient(Long id, ClientRequestDTO updatedClient);

    ClientResponseDTO toDto(Client Client);

    Client toEntity(ClientRequestDTO dto);
}
