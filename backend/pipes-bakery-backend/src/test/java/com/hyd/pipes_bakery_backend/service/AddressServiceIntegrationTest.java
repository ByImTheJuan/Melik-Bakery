package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AddressServiceIntegrationTest {

    @Autowired
    private AddressService addressService;

    @Test
    void shouldCreateAndRetrieveClient() {
        AddressRequestDTO request = new AddressRequestDTO();
        request.setStreet("Calle 134");
        request.setAdditionalInformation("Apto 201");
        request.setCity("Bogotá");
        request.setCountry("Colombia");
        request.setZipCode(110121);

        AddressResponseDTO created = addressService.createAddress(request);

        AddressResponseDTO found =
                addressService.getAddressById(created.getId());

        assertThat(found.getStreet()).isEqualTo("Calle 134");
        assertThat(found.getAdditionalInformation()).isEqualTo("Apto 201");
        assertThat(found.getCity()).isEqualTo("Bogotá");
        assertThat(found.getCountry()).isEqualTo("Colombia");
        assertThat(found.getZipCode()).isEqualTo(110121);
    }
}