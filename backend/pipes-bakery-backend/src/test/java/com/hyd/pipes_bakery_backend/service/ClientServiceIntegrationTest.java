package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hyd.pipes_bakery_backend.dto.client.ClientRequestDTO;
import com.hyd.pipes_bakery_backend.dto.client.ClientResponseDTO;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ClientServiceIntegrationTest {

    @Autowired
    private ClientService clientService;

    @Test
    void shouldCreateAndRetrieveClient() {
        ClientRequestDTO request = new ClientRequestDTO();
        request.setFirstName("Felipe");
        request.setLastName("Hernández");
        request.setEmail("pipelon@gmail.com");
        request.setPhoneNumber("3053466622");

        ClientResponseDTO created = clientService.createClient(request);

        ClientResponseDTO found =
                clientService.getClientById(created.getId());

        assertThat(found.getFirstName()).isEqualTo("Felipe");
        assertThat(found.getLastName()).isEqualTo("Hernández");
        assertThat(found.getEmail()).isEqualTo("pipelon@gmail.com");
        assertThat(found.getPhoneNumber()).isEqualTo("3053466622");
    }
}
