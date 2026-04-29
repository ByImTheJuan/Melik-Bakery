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
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.service.AddressService;


@SuppressWarnings("null")
@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAddressSuccessfully() throws Exception {

        // Arrange
        AddressRequestDTO request = new AddressRequestDTO();
        request.setStreet("Calle 134");
        request.setAdditionalInformation("Apto 201");
        request.setCity("Bogotá");
        request.setCountry("Colombia");
        request.setZipCode(110121);

        AddressResponseDTO response = new AddressResponseDTO(
                1L,
                "Calle 134",
                "Apto 201",
                "Bogotá",
                110121,
                "Colombia"
        );

        when(addressService.createAddress(any(AddressRequestDTO.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.street").value("Calle 134"))
                .andExpect(jsonPath("$.additionalInformation").value("Apto 201"))
                .andExpect(jsonPath("$.city").value("Bogotá"))
                .andExpect(jsonPath("$.zipCode").value(110121))
                .andExpect(jsonPath("$.country").value("Colombia"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(addressService).createAddress(any(AddressRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingAddressWithInvalidData() throws Exception {

        // Arrange
        AddressRequestDTO request = new AddressRequestDTO();
        request.setStreet(""); // Invalid street
        request.setAdditionalInformation(""); // Invalid additional information
        request.setCity(""); // Invalid city
        request.setZipCode(0); // Invalid zip code
        request.setCountry(""); // Invalid country

        // Act + Assert
        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details").isNotEmpty());
    }

    @Test
    void shouldGetAddressByIdSuccessfully() throws Exception {

        // Arrange
        Long addressId = 1L;
        AddressResponseDTO response = new AddressResponseDTO(
                addressId,
                "Avenida 7",
                "Casa 11",
                "Medellín",
                110121,
                "Colombia"
        );

        when(addressService.getAddressById(addressId)).thenReturn(response);

        // Act + Assert
        mockMvc.perform(get("/api/addresses/{id}", addressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId))
                .andExpect(jsonPath("$.street").value("Avenida 7"))
                .andExpect(jsonPath("$.additionalInformation").value("Casa 11"))
                .andExpect(jsonPath("$.city").value("Medellín"))
                .andExpect(jsonPath("$.zipCode").value(110121))
                .andExpect(jsonPath("$.country").value("Colombia"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(addressService).getAddressById(addressId);
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistingAddress() throws Exception {

        // Arrange
        Long addressId = 999L;

        when(addressService.getAddressById(addressId))
                        .thenThrow(new ResourceNotFoundException("Address not found with id " + addressId));

        // Act + Assert
        mockMvc.perform(get("/api/addresses/{id}", addressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Address not found with id " + addressId));

        verify(addressService).getAddressById(addressId);
    }

    @Test
    void shouldUpdateAddressSuccessfully() throws Exception {
        // Arrange
        Long addressId = 1L;
        AddressRequestDTO request = new AddressRequestDTO();
        request.setStreet("Calle 134");
        request.setAdditionalInformation("Casa 11");
        request.setCity("Medellín");
        request.setZipCode(110121);
        request.setCountry("Colombia");

        AddressResponseDTO response = new AddressResponseDTO(
                addressId,
                "Carrera 7A",
                "Apto 203",
                "Bogotá",
                000000,
                "Ecuador"
        );

        when(addressService.updateAddress(anyLong(), any(AddressRequestDTO.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(put("/api/addresses/{id}", addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId))
                .andExpect(jsonPath("$.street").value("Carrera 7A"))
                .andExpect(jsonPath("$.additionalInformation").value("Apto 203"))
                .andExpect(jsonPath("$.city").value("Bogotá"))
                .andExpect(jsonPath("$.zipCode").value(000000))
                .andExpect(jsonPath("$.country").value("Ecuador"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(addressService).updateAddress(anyLong(), any(AddressRequestDTO.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingAddress() throws Exception {
        // Arrange
        Long addressId = 999L;
        AddressRequestDTO request = new AddressRequestDTO();
        request.setStreet("Calle 134");
        request.setAdditionalInformation("Casa 11");
        request.setCity("Medellín");
        request.setZipCode(110121);
        request.setCountry("Colombia");

        when(addressService.updateAddress(anyLong(), any(AddressRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Address not found with id " + addressId));

        // Act + Assert
        mockMvc.perform(put("/api/addresses/{id}", addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Address not found with id " + addressId));

        verify(addressService).updateAddress(anyLong(), any(AddressRequestDTO.class));
    }

    @Test
    void shouldDeleteAddressSuccessfully() throws Exception {
        // Arrange
        Long addressId = 1L;

        // Act + Assert
        mockMvc.perform(delete("/api/addresses/{id}", addressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(addressService).deleteAddress(addressId);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingAddress() throws Exception {
        // Arrange
        Long addressId = 999L;

        doThrow(new ResourceNotFoundException("Address not found with id " + addressId))
                .when(addressService).deleteAddress(addressId);

        // Act + Assert
        mockMvc.perform(delete("/api/addresses/{id}", addressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Address not found with id " + addressId));

        verify(addressService).deleteAddress(addressId);
    }
}
