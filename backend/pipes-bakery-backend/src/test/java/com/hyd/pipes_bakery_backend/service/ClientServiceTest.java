package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.ClientMapper;
import com.hyd.pipes_bakery_backend.mapper.OrderMapper;
import com.hyd.pipes_bakery_backend.model.Client;
import com.hyd.pipes_bakery_backend.repository.ClientRepository;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;


    @SuppressWarnings("null")
    @Test
    void shouldCreateClientSuccessfully() {
        // Arrange
        ClientRequestDTO request = new ClientRequestDTO();

        request.setFirstName("Felipe");
        request.setLastName("Hernández");
        request.setEmail("pipelon@gmail.com");
        request.setPassword("password123");
        request.setPhoneNumber("3053466622");

        Client savedClient = new Client();
        savedClient.setFirstName("Felipe");
        savedClient.setLastName("Hernández");
        savedClient.setEmail("pipelon@gmail.com");
        savedClient.setPhoneNumber("3053466622");
        savedClient.setId(1L);

        ClientResponseDTO responseDto = new ClientResponseDTO(1L, 
                                                                "Felipe", 
                                                                "Hernández", 
                                                                "pipelon@gmail.com", 
                                                                "3053466622",
                                                                null);

        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        when(clientMapper.toDto(savedClient)).thenReturn(responseDto);
        when(clientMapper.toEntity(request)).thenReturn(savedClient);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");

        // Act
        ClientResponseDTO result = clientService.createClient(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Felipe");
        assertThat(result.getLastName()).isEqualTo("Hernández");
        assertThat(result.getEmail()).isEqualTo("pipelon@gmail.com");
        assertThat(result.getPhoneNumber()).isEqualTo("3053466622");

        verify(clientRepository).save(any(Client.class));
    }


    @Test
    void shouldGetClientByIdSuccessfully() {
        
        // Arrange
        Long clientId = 1L;
        Client client = new Client();
        client.setId(clientId);
        client.setFirstName("Felipe");
        client.setLastName("Hernández");
        client.setEmail("pipelon@gmail.com");
        client.setPhoneNumber("3053466622");

        ClientResponseDTO responseDto = new ClientResponseDTO(1L, 
                                                                "Felipe", 
                                                                "Hernández", 
                                                                "pipelon@gmail.com", 
                                                                "3053466622",
                                                                null);

        when(clientMapper.toDto(client)).thenReturn(responseDto);
        when(clientRepository.findById(clientId)).thenReturn(java.util.Optional.of(client));

        // Act
        ClientResponseDTO result = clientService.getClientById(clientId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clientId);
        assertThat(result.getFirstName()).isEqualTo("Felipe");
        assertThat(result.getLastName()).isEqualTo("Hernández");
        assertThat(result.getEmail()).isEqualTo("pipelon@gmail.com");
        assertThat(result.getPhoneNumber()).isEqualTo("3053466622");

        verify(clientRepository).findById(clientId);
    }


    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        
        // Arrange
        Long clientId = 999L;

        when(clientRepository.findById(clientId)).thenReturn(java.util.Optional.empty());


        // Act & Assert
        try {
            clientService.getClientById(clientId);
        } catch (ResourceNotFoundException e) {
            assertThat(e).isInstanceOf(com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Client not found with id " + clientId);
        }

        verify(clientRepository).findById(clientId);
    }


    @Test
    void shouldDeleteClientSuccessfully() {
        
        // Arrange
        Long clientId = 1L;

        when(clientRepository.existsById(clientId)).thenReturn(true);

        // Act
        clientService.deleteClient(clientId);

        // Assert
        verify(clientRepository).existsById(clientId);
        verify(clientRepository).deleteById(clientId);
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistentClient() {
        
        // Arrange
        Long clientId = 999L;

        when(clientRepository.existsById(clientId)).thenReturn(false);


        // Act & Assert
        try {
            clientService.deleteClient(clientId);
        } catch (ResourceNotFoundException e) {
            assertThat(e).isInstanceOf(com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Client not found with id " + clientId);
        }

        verify(clientRepository).existsById(clientId);
    }

    
    @SuppressWarnings("null")
    @Test
    void shouldUpdateClientSuccessfully() {
        
        // Arrange
        Long clientId = 1L;
        ClientRequestDTO updatedRequest = new ClientRequestDTO();
        updatedRequest.setFirstName("Pan de chocolate");
        updatedRequest.setLastName("Hernández");
        updatedRequest.setEmail("panchocolate@gmail.com");
        updatedRequest.setPassword("password123");
        updatedRequest.setPhoneNumber("3053466622");

        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setFirstName("Pan simple");
        existingClient.setLastName("Fernández");
        existingClient.setEmail("pan@gmail.com");
        existingClient.setPhoneNumber("3053466922");

        ClientResponseDTO responseDto = new ClientResponseDTO(1L, 
                                                                "Pan de chocolate", 
                                                                "Hernández", 
                                                                "panchocolate@gmail.com", 
                                                                "3053466622",
                                                                null);

        when(clientRepository.findById(clientId)).thenReturn(java.util.Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));
        when(clientMapper.toDto(any(Client.class))).thenReturn(responseDto);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$updatedHashedPassword");

        // Act
        ClientResponseDTO result = clientService.updateClient(clientId, updatedRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Pan de chocolate");
        assertThat(result.getLastName()).isEqualTo("Hernández");
        assertThat(result.getEmail()).isEqualTo("panchocolate@gmail.com");
        assertThat(result.getPhoneNumber()).isEqualTo("3053466622");

        verify(clientRepository).findById(clientId);
        verify(clientRepository).save(any(Client.class));
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentClient() {
        
        // Arrange
        Long clientId = 999L;
        ClientRequestDTO updatedRequest = new ClientRequestDTO();
        updatedRequest.setFirstName("Pan inexistente");
        updatedRequest.setLastName("Fernández");
        updatedRequest.setEmail("paninexistente@gmail.com");
        updatedRequest.setPassword("password123");
        updatedRequest.setPhoneNumber("3053466922");

        ClientResponseDTO responseDto = new ClientResponseDTO(1L, 
                                                                "Pan de chocolate", 
                                                                "Hernández", 
                                                                "panchocolate@gmail.com", 
                                                                "3053466622",
                                                                null);

        when(clientRepository.findById(clientId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            clientService.updateClient(clientId, updatedRequest);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Client not found");
        }

        verify(clientRepository).findById(clientId);
    }

    @Test
    void shouldGetAllClientsSuccessfully() {
        
        // Arrange
        Client client1 = new Client();
        client1.setId(1L);
        client1.setFirstName("Felipe");
        client1.setLastName("Hernández");
        client1.setEmail("felipe@gmail.com");
        client1.setPhoneNumber("3053466622");

        Client client2 = new Client();
        client2.setId(2L);
        client2.setFirstName("Juande");
        client2.setLastName("Hernández");
        client2.setEmail("jd@gmail.com");
        client2.setPhoneNumber("3053466922");

        ClientResponseDTO responseDto = new ClientResponseDTO(1L, 
                                                                "Felipe", 
                                                                "Hernández", 
                                                                "pipelon@gmail.com", 
                                                                "3053466622",
                                                                null);

        when(clientMapper.toDto(client1)).thenReturn(responseDto);
        when(clientMapper.toDto(client2)).thenReturn(new ClientResponseDTO(2L, 
                                                                "Juande", 
                                                                "Hernández", 
                                                                "jd@gmail.com", 
                                                                "3053466922",
                                                                null));

        when(clientRepository.findAll()).thenReturn(java.util.List.of(client1, client2));

        // Act
        java.util.List<ClientResponseDTO> result = clientService.getAllClients();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);

        verify(clientRepository).findAll();
    }

}
