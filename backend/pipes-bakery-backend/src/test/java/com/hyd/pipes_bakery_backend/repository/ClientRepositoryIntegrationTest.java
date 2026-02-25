package com.hyd.pipes_bakery_backend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hyd.pipes_bakery_backend.model.Client;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ClientRepositoryIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void shouldSaveAndFindClient() {
        Client client = new Client();
        client.setFirstName("Felipe");
        client.setLastName("Hernández");
        client.setEmail("pipelon@gmail.com");
        client.setPhoneNumber("3053466622");
        Client saved = clientRepository.save(client);

        assertThat(saved.getId()).isNotNull();

        Client found = clientRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getFirstName()).isEqualTo("Felipe");
        assertThat(found.getLastName()).isEqualTo("Hernández");
        assertThat(found.getEmail()).isEqualTo("pipelon@gmail.com");
        assertThat(found.getPhoneNumber()).isEqualTo("3053466622");
    }
}