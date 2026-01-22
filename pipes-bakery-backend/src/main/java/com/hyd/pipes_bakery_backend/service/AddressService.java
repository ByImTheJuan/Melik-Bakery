package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.model.Address;
import com.hyd.pipes_bakery_backend.repository.AddressRepository;

@Service
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public AddressResponseDTO getAddressById(Long id) {

        return addressRepository.findById(id).map(this::toDto).orElseThrow(() -> new ResourceNotFoundException(
                "Address not found with id " + id
        ));
    }

    @Override
    public AddressResponseDTO createAddress(AddressRequestDTO dto) {
        Address address = toEntity(dto);
        Address savedAddress = addressRepository.save(address);
        return toDto(savedAddress);
    }

    @Override
    public void deleteAddress(Long id) {
        if(addressRepository.existsById(id))
            addressRepository.deleteById(id);

        else
            throw new ResourceNotFoundException("Address not found with id " + id);
    }

    @Override
    public AddressResponseDTO updateAddress(Long id, AddressRequestDTO updatedAddress) {
            return addressRepository.findById(id)
                .map(address -> {
                    address.setStreet(updatedAddress.getStreet());
                    address.setAdditionalInformation(updatedAddress.getAdditionalInformation());
                    address.setCity(updatedAddress.getCity());
                    address.setCountry(updatedAddress.getCountry());
                    address.setZipCode(updatedAddress.getZipCode());
                    return addressRepository.save(address);
                })
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Address not found with id " + id
            ));
    }

    @Override
    public AddressResponseDTO toDto(Address address) {
        AddressResponseDTO dto = new AddressResponseDTO(address.getId(), 
                                                        address.getStreet(), 
                                                        address.getAdditionalInformation(), 
                                                        address.getCity(), 
                                                        address.getZipCode(), 
                                                        address.getCountry());
        return dto;
    }

    @Override
    public Address toEntity(AddressRequestDTO dto) {
        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setAdditionalInformation(dto.getAdditionalInformation());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        return address;
    }
}
