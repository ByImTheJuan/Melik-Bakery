package com.hyd.pipes_bakery_backend.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndRetrieveClient() throws Exception {
        ClientRequestDTO request = new ClientRequestDTO();
        request.setFirstName("Felipe");
        request.setLastName("Hernández");
        request.setEmail("pipelon@gmail.com");
        request.setPhoneNumber("3053466622");

        AddressRequestDTO addressRequest = new AddressRequestDTO();
        addressRequest.setStreet("Calle 134");
        addressRequest.setAdditionalInformation("Casa 24");
        addressRequest.setCity("Bogotá");
        addressRequest.setZipCode(654321);
        addressRequest.setCountry("Colombia");

        request.setAddress(addressRequest);

        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/api/clients/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Felipe"))
            .andExpect(jsonPath("$.lastName").value("Hernández"))
            .andExpect(jsonPath("$.email").value("pipelon@gmail.com"))
            .andExpect(jsonPath("$.phoneNumber").value("3053466622"));
    }
}
