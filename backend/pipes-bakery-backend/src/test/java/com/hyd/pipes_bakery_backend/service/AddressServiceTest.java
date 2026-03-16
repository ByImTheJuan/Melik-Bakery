package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;
import com.hyd.pipes_bakery_backend.mapper.AddressMapper;
import com.hyd.pipes_bakery_backend.model.Address;
import com.hyd.pipes_bakery_backend.repository.AddressRepository;


@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    private final AddressMapper addressMapper = new AddressMapper();

    private AddressService addressService;

    @BeforeEach
        void setUp() {
            addressService = new AddressService(addressRepository, addressMapper);
        }
    
    @SuppressWarnings("null")
    @Test
    void shouldCreateAddressSuccessfully() {
        // Arrange
        AddressRequestDTO request = new AddressRequestDTO();

        request.setStreet("Calle 134");
        request.setAdditionalInformation("Apto 201");
        request.setCity("Medellín");
        request.setCountry("Colombia");
        request.setZipCode(110121);

        Address savedAddress = new Address();
        savedAddress.setStreet("Calle 134");
        savedAddress.setAdditionalInformation("Apto 201");
        savedAddress.setCity("Medellín");
        savedAddress.setCountry("Colombia");
        savedAddress.setZipCode(110121);
        savedAddress.setId(1L);

        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);

        // Act
        AddressResponseDTO result = addressService.createAddress(request);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStreet()).isEqualTo("Calle 134");
        assertThat(result.getAdditionalInformation()).isEqualTo("Apto 201");
        assertThat(result.getCity()).isEqualTo("Medellín");
        assertThat(result.getCountry()).isEqualTo("Colombia");
        assertThat(result.getZipCode()).isEqualTo(110121);

        verify(addressRepository).save(any(Address.class));
    }


    @Test
    void shouldGetAddressByIdSuccessfully() {
        
        // Arrange
        Long addressId = 1L;
        Address address = new Address();
        address.setId(addressId);
        address.setStreet("Calle 134");
        address.setAdditionalInformation("Apto 201");
        address.setCity("Medellín");
        address.setCountry("Colombia");
        address.setZipCode(110121);

        when(addressRepository.findById(addressId)).thenReturn(java.util.Optional.of(address));

        // Act
        AddressResponseDTO result = addressService.getAddressById(addressId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(addressId);
        assertThat(result.getStreet()).isEqualTo("Calle 134");
        assertThat(result.getAdditionalInformation()).isEqualTo("Apto 201");
        assertThat(result.getCity()).isEqualTo("Medellín");
        assertThat(result.getCountry()).isEqualTo("Colombia");
        assertThat(result.getZipCode()).isEqualTo(110121);

        verify(addressRepository).findById(addressId);
    }


    @Test
    void shouldThrowExceptionWhenAddressNotFound() {
        
        // Arrange
        Long addressId = 999L;

        when(addressRepository.findById(addressId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            addressService.getAddressById(addressId);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Address not found with id " + addressId);
        }

        verify(addressRepository).findById(addressId);
    }


    @Test
    void shouldDeleteAddressSuccessfully() {
        
        // Arrange
        Long addressId = 1L;

        when(addressRepository.existsById(addressId)).thenReturn(true);

        // Act
        addressService.deleteAddress(addressId);

        // Assert
        verify(addressRepository).existsById(addressId);
        verify(addressRepository).deleteById(addressId);
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistentAddress() {
        
        // Arrange
        Long addressId = 999L;

        when(addressRepository.existsById(addressId)).thenReturn(false);

        // Act & Assert
        try {
            addressService.deleteAddress(addressId);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Address not found with id " + addressId);
        }

        verify(addressRepository).existsById(addressId);
    }

    
    @SuppressWarnings("null")
    @Test
    void shouldUpdateAddressSuccessfully() {
        
        // Arrange
        Long addressId = 1L;
        AddressRequestDTO updatedRequest = new AddressRequestDTO();
        updatedRequest.setStreet("Calle 134");
        updatedRequest.setAdditionalInformation("Apto 201");
        updatedRequest.setCity("Medellín");
        updatedRequest.setCountry("Colombia");
        updatedRequest.setZipCode(110121);

        Address existingAddress = new Address();
        existingAddress.setId(addressId);
        existingAddress.setStreet("Calle 134");
        existingAddress.setAdditionalInformation("Apto 201");
        existingAddress.setCity("Medellín");
        existingAddress.setCountry("Colombia");
        existingAddress.setZipCode(110121);

        when(addressRepository.findById(addressId)).thenReturn(java.util.Optional.of(existingAddress));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        AddressResponseDTO result = addressService.updateAddress(addressId, updatedRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStreet()).isEqualTo("Calle 134");
        assertThat(result.getAdditionalInformation()).isEqualTo("Apto 201");
        assertThat(result.getCity()).isEqualTo("Medellín");
        assertThat(result.getCountry()).isEqualTo("Colombia");
        assertThat(result.getZipCode()).isEqualTo(110121);

        verify(addressRepository).findById(addressId);
        verify(addressRepository).save(any(Address.class));
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentAddress() {
        
        // Arrange
        Long addressId = 999L;
        AddressRequestDTO updatedRequest = new AddressRequestDTO();
        updatedRequest.setStreet("Calle 134");
        updatedRequest.setAdditionalInformation("Apto 201");
        updatedRequest.setCity("Medellín");
        updatedRequest.setCountry("Colombia");
        updatedRequest.setZipCode(110121);

        when(addressRepository.findById(addressId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            addressService.updateAddress(addressId, updatedRequest);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Address not found with id " + addressId);
        }

        verify(addressRepository).findById(addressId);
    }

    @Test
    void shouldGetAllAddressesSuccessfully() {
        
        // Arrange
        Address address1 = new Address();
        address1.setId(1L);
        address1.setStreet("Calle 134");
        address1.setAdditionalInformation("Apto 201");
        address1.setCity("Medellín");
        address1.setCountry("Colombia");
        address1.setZipCode(110121);

        Address address2 = new Address();
        address2.setId(2L);
        address2.setStreet("Calle 127");
        address2.setAdditionalInformation("Apto 503");
        address2.setCity("Bogotá");
        address2.setCountry("Colombia");
        address2.setZipCode(110121);

        when(addressRepository.findAll()).thenReturn(java.util.List.of(address1, address2));

        // Act
        java.util.List<AddressResponseDTO> result = addressService.getAllAddresses();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);

        verify(addressRepository).findAll();
    }

}