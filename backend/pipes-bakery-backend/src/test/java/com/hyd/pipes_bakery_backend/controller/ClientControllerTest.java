package com.hyd.pipes_bakery_backend.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.service.ClientService;

@SuppressWarnings("null")
@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateClientSuccessfully() throws Exception {

        // Arrange
        AddressRequestDTO addressRequest = new AddressRequestDTO();
        addressRequest.setStreet("Calle 123");
        addressRequest.setAdditionalInformation("Apto 201");
        addressRequest.setCity("Bogotá");
        addressRequest.setCountry("Colombia");
        addressRequest.setZipCode(123456);

        ClientRequestDTO request = new ClientRequestDTO();
        request.setFirstName("Felipe");
        request.setLastName("Hernández");
        request.setEmail("pipelon@gmail.com");
        request.setPassword("password123");
        request.setPhoneNumber("3053466622");
        request.setAddress(addressRequest);


        AddressResponseDTO addressResponse = new AddressResponseDTO(
                1L,
                "Calle 123",
                "Apto 201",
                "Bogotá",
                123456,
                "Colombia"
        );

        ClientResponseDTO response = new ClientResponseDTO(
                1L,
                "Felipe",
                "Hernández",
                "pipelon@gmail.com",
                "3053466622",
                addressResponse
        );

        when(clientService.createClient(any(ClientRequestDTO.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Felipe"))
                .andExpect(jsonPath("$.lastName").value("Hernández"))
                .andExpect(jsonPath("$.email").value("pipelon@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("3053466622"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(clientService).createClient(any(ClientRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingClientWithInvalidData() throws Exception {

        // Arrange
        ClientRequestDTO request = new ClientRequestDTO();
        request.setFirstName(""); // Invalid name
        request.setLastName(""); // Invalid last name
        request.setEmail("invalid-email"); // Invalid email
        request.setPassword("short");
        request.setPhoneNumber("invalid-phone"); // Invalid phone

        // Act + Assert
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldGetClientByIdSuccessfully() throws Exception {

        // Arrange
        Long clientId = 1L;

        AddressResponseDTO addressResponse = new AddressResponseDTO(
                1L,
                "Calle 123",
                "Apto 201",
                "Bogotá",
                123456,
                "Colombia"
        );
        ClientResponseDTO response = new ClientResponseDTO(
                clientId,
                "Baguette",
                "García",
                "pipelon@gmail.com",
                "3053466622",
                addressResponse
        );

        when(clientService.getClientById(clientId)).thenReturn(response);

        // Act + Assert
        mockMvc.perform(get("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.firstName").value("Baguette"))
                .andExpect(jsonPath("$.lastName").value("García"))
                .andExpect(jsonPath("$.email").value("pipelon@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("3053466622"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(clientService).getClientById(clientId);
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistingClient() throws Exception {

        // Arrange
        Long clientId = 999L;

        when(clientService.getClientById(clientId))
                        .thenThrow(new ResourceNotFoundException("Client not found with id " + clientId));

        // Act + Assert
        mockMvc.perform(get("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Client not found with id " + clientId));

        verify(clientService).getClientById(clientId);
    }

    @Test
    void shouldUpdateClientSuccessfully() throws Exception {
        // Arrange
        Long clientId = 1L;
        ClientRequestDTO request = new ClientRequestDTO();
        request.setFirstName("Baguette");
        request.setLastName("García");
        request.setEmail("pipelon@gmail.com");
        request.setPassword("password123");
        request.setPhoneNumber("3053466622");

        AddressRequestDTO addressRequest = new AddressRequestDTO();
        addressRequest.setStreet("Calle 134");
        addressRequest.setAdditionalInformation("Casa 24");
        addressRequest.setCity("Bogotá");
        addressRequest.setZipCode(654321);
        addressRequest.setCountry("Colombia");

        request.setAddress(addressRequest);


        AddressResponseDTO addressResponse = new AddressResponseDTO(
                1L,
                "Calle 123",
                "Apto 201",
                "Bogotá",
                123456,
                "Colombia"
        );
        ClientResponseDTO response = new ClientResponseDTO(
                clientId,
                "Felipe",
                "Hernández",
                "pipeloncho@gmail.com",
                "3053466922",
                addressResponse
        );

        when(clientService.updateClient(anyLong(), any(ClientRequestDTO.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(put("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.firstName").value("Felipe"))
                .andExpect(jsonPath("$.lastName").value("Hernández"))
                .andExpect(jsonPath("$.email").value("pipeloncho@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("3053466922"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(clientService).updateClient(anyLong(), any(ClientRequestDTO.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingClient() throws Exception {
        // Arrange
        Long clientId = 999L;
        ClientRequestDTO request = new ClientRequestDTO();
        request.setFirstName("Baguette");
        request.setLastName("García");
        request.setEmail("pipelon@gmail.com");
        request.setPassword("password123");
        request.setPhoneNumber("3053466622");

        AddressRequestDTO addressRequest = new AddressRequestDTO();
        addressRequest.setStreet("Calle 134");
        addressRequest.setAdditionalInformation("Casa 24");
        addressRequest.setCity("Bogotá");
        addressRequest.setZipCode(654321);
        addressRequest.setCountry("Colombia");

        request.setAddress(addressRequest);

        when(clientService.updateClient(anyLong(), any(ClientRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Client not found with id " + clientId));

        // Act + Assert
        mockMvc.perform(put("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Client not found with id " + clientId));


        verify(clientService).updateClient(anyLong(), any(ClientRequestDTO.class));
    }

    @Test
    void shouldDeleteClientSuccessfully() throws Exception {
        // Arrange
        Long clientId = 1L;

        // Act + Assert
        mockMvc.perform(delete("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(clientService).deleteClient(clientId);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingClient() throws Exception {
        // Arrange
        Long clientId = 999L;

        doThrow(new ResourceNotFoundException("Client not found with id " + clientId))
                .when(clientService).deleteClient(clientId);

        // Act + Assert
        mockMvc.perform(delete("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Client not found with id " + clientId));

        verify(clientService).deleteClient(clientId);
    }
}
