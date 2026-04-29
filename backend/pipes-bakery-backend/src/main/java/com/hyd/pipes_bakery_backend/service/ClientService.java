package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientResponseDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.exception.DuplicateResourceException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.ClientMapper;
import com.hyd.pipes_bakery_backend.mapper.OrderMapper;
import com.hyd.pipes_bakery_backend.model.Client;
import com.hyd.pipes_bakery_backend.repository.ClientRepository;

@Service
public class ClientService implements IClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final OrderMapper orderMapper;
    private final PasswordEncoder passwordEncoder;

    public ClientService(
            ClientRepository clientRepository,
            ClientMapper clientMapper,
            OrderMapper orderMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.orderMapper = orderMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll()
                    .stream()
                    .map(clientMapper::toDto)
                    .toList();
    }

    @Override
    public ClientResponseDTO getClientById(@NonNull Long id) {

        return clientRepository.findById(id).map(clientMapper::toDto).orElseThrow(() -> new ResourceNotFoundException(
                "Client not found with id " + id
        ));
    }   

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        if(clientRepository.existsByEmail(dto.getEmail())){
            throw new DuplicateResourceException("Email already registered");
        }
        
        Client client = clientMapper.toEntity(dto);
        client.setPassword(passwordEncoder.encode(dto.getPassword()));
        Client savedClient = clientRepository.save(client);
        return clientMapper.toDto(savedClient);
    }

    @Override
    public void deleteClient(@NonNull Long id) {
        if(clientRepository.existsById(id))
            clientRepository.deleteById(id);

        else
            throw new ResourceNotFoundException("Client not found with id " + id);
    }

    @Override
    public ClientResponseDTO updateClient(@NonNull Long id, ClientRequestDTO updatedClient) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setFirstName(updatedClient.getFirstName());
                    client.setLastName(updatedClient.getLastName());
                    client.setEmail(updatedClient.getEmail());
                    client.setPassword(passwordEncoder.encode(updatedClient.getPassword()));
                    client.setPhoneNumber(updatedClient.getPhoneNumber());
                    return clientRepository.save(client);
                })
                .map(clientMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }

    @Override
    public List<OrderResponseDTO> getAllOrders(@NonNull Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + clientId));
        
        return client.getOrders()
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }
}
