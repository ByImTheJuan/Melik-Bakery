package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.model.Client;
import com.hyd.pipes_bakery_backend.repository.ClientRepository;

@Service
public class ClientService implements IClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll()
                    .stream()
                    .map(this::toDto)
                    .toList();
    }

    @Override
    public ClientResponseDTO getClientById(Long id) {

        return clientRepository.findById(id).map(this::toDto).orElseThrow(() -> new ResourceNotFoundException(
                "Client not found with id " + id
        ));
    }

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        Client client = toEntity(dto);
        Client savedClient = clientRepository.save(client);
        return toDto(savedClient);
    }

    @Override
    public void deleteClient(Long id) {
        if(clientRepository.existsById(id))
            clientRepository.deleteById(id);

        else
            throw new ResourceNotFoundException("Client not found with id " + id);
    }

    @Override
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO updatedClient) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setFirstName(updatedClient.getFirstName());
                    client.setLastName(updatedClient.getLastName());
                    client.setEmail(updatedClient.getEmail());
                    client.setPhoneNumber(updatedClient.getPhoneNumber());
                    return clientRepository.save(client);
                })
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }

    @Override
    public ClientResponseDTO toDto(Client client) {
        ClientResponseDTO dto = new ClientResponseDTO(client.getId(), 
                                                        client.getFirstName(),
                                                        client.getLastName(), 
                                                        client.getEmail(), 
                                                        client.getPhoneNumber());
        return dto;
    }

    @Override
    public Client toEntity(ClientRequestDTO dto) {
        Client client = new Client();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setPhoneNumber(dto.getPhoneNumber());
        return client;
    }
}
