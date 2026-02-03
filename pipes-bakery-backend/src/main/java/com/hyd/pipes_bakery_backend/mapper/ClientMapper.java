package com.hyd.pipes_bakery_backend.mapper;

import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientResponseDTO;
import com.hyd.pipes_bakery_backend.model.Client;

@Component
public class ClientMapper {

    private final AddressMapper addressMapper = new AddressMapper();


    public ClientResponseDTO toDto(Client client) {
        ClientResponseDTO dto = new ClientResponseDTO(client.getId(), 
                                                        client.getFirstName(),
                                                        client.getLastName(), 
                                                        client.getEmail(), 
                                                        client.getPassword(),
                                                        client.getPhoneNumber(),
                                                        addressMapper.toDto(client.getAddress()));
        return dto;
    }


    public Client toEntity(ClientRequestDTO dto) {
        Client client = new Client();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setPhoneNumber(dto.getPhoneNumber());
        client.setPassword(dto.getPassword());
        client.setAddress(addressMapper.toEntity(dto.getAddress()));
        return client;
    }
}
