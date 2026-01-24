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

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndRetrieveAddress() throws Exception {
        AddressRequestDTO request = new AddressRequestDTO();
        request.setStreet("Calle 134");
        request.setAdditionalInformation("Apto 201");
        request.setCity("Bogotá");
        request.setZipCode(110121);
        request.setCountry("Colombia");

        mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/api/addresses/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.street").value("Calle 134"))
            .andExpect(jsonPath("$.additionalInformation").value("Apto 201"))
            .andExpect(jsonPath("$.city").value("Bogotá"))
            .andExpect(jsonPath("$.zipCode").value(110121))
            .andExpect(jsonPath("$.country").value("Colombia"));
    }
}
