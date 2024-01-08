package com.example.directoryservice.service;

import com.example.directoryservice.dto.ServiceRequestDto;
import com.example.directoryservice.dto.ServiceResponseDto;
import com.example.directoryservice.model.Service;
import com.example.directoryservice.repository.AddressRepository;
import com.example.directoryservice.repository.OrganisationRepository;
import com.example.directoryservice.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final AddressRepository addressRepository;
    private final OrganisationRepository organisationRepository;

    @Transactional
    public Integer createService(ServiceRequestDto serviceRequestDto)
            throws DataIntegrityViolationException {
        // create new entity
        Service service = Service.builder()
                .name(serviceRequestDto.getName())
                .address(addressRepository.findById(serviceRequestDto.getAddress())
                        .orElseThrow(() -> new DataIntegrityViolationException("")))
                .organisation(organisationRepository.findByName(serviceRequestDto.getOrganisation())
                        .orElseThrow(() -> new DataIntegrityViolationException("")))
                .build();

        // try to save entity and return its id
        return serviceRepository.save(service).getId();
    }

    public List<ServiceResponseDto> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(ServiceService::modelToResponseDto)
                .toList();
    }

    public ServiceResponseDto getService(Integer idService) throws NoSuchElementException {
        return serviceRepository.findById(idService)
                .map(ServiceService::modelToResponseDto)
                .orElseThrow();
    }

    public void updateServiceAddress(Integer idService, Integer idAddress)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update the address
        int row = serviceRepository.updateAddressById(idService, idAddress);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public void updateServiceOrganisation(Integer idService, Integer idOrganisation)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update the address
        int row = serviceRepository.updateOrganisationById(idService, idOrganisation);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public void deleteServiceById(Integer idService) throws NoSuchElementException {
        if (! serviceRepository.existsById(idService))
            throw new NoSuchElementException();
        serviceRepository.deleteById(idService);
    }

    private static ServiceResponseDto modelToResponseDto(Service service) {
        return ServiceResponseDto.builder()
                .id(service.getId())
                .name(service.getName())
                .address(service.getAddress())
                .organisation(service.getOrganisation().getName())
                .employees(service.getEmployees())
                .build();
    }

}
