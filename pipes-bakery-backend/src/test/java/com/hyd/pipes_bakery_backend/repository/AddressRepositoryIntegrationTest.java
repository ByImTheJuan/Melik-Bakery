package com.hyd.pipes_bakery_backend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hyd.pipes_bakery_backend.model.Address;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AddressRepositoryIntegrationTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    void shouldSaveAndFindAddress() {
        Address address = new Address();
        address.setStreet("Calle 123");
        address.setAdditionalInformation("Apto 201");
        address.setCity("Medellín");
        address.setCountry("Colombia");
        address.setZipCode(050001);

        Address saved = addressRepository.save(address);

        assertThat(saved.getId()).isNotNull();

        Address found = addressRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getStreet()).isEqualTo("Calle 123");
        assertThat(found.getAdditionalInformation()).isEqualTo("Apto 201");
        assertThat(found.getCity()).isEqualTo("Medellín");
        assertThat(found.getCountry()).isEqualTo("Colombia");
        assertThat(found.getZipCode()).isEqualTo("050001");
    }
}