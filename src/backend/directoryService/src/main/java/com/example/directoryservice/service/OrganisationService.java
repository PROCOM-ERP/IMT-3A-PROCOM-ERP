package com.example.directoryservice.service;

import com.example.directoryservice.dto.OrganisationRequestDto;
import com.example.directoryservice.dto.OrganisationResponseDto;
import com.example.directoryservice.model.Organisation;
import com.example.directoryservice.repository.AddressRepository;
import com.example.directoryservice.repository.OrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public String createOrganisation(OrganisationRequestDto organisationRequestDto) {
        // create new entity
        Organisation organisation = Organisation.builder()
                .name(organisationRequestDto.getName())
                .address(addressRepository.findById(organisationRequestDto.getAddress())
                        .orElseThrow(() -> new DataIntegrityViolationException("")))
                .build();

        // try to save entity and return its id
        return organisationRepository.save(organisation).getName();
    }

    public List<OrganisationResponseDto> getAllOrganisations() {
        return organisationRepository.findAll().stream()
                .map(OrganisationService::modelToResponseDto)
                .toList();
    }

    public OrganisationResponseDto getOrganisationById(Integer idOrganisation) {
        return organisationRepository.findById(idOrganisation)
                .map(OrganisationService::modelToResponseDto)
                .orElseThrow();
    }

    public OrganisationResponseDto getOrganisationByName(String name) {
        return organisationRepository.findByName(name)
                .map(OrganisationService::modelToResponseDto)
                .orElseThrow();
    }

    public void updateOrganisationAddressById(Integer idOrganisation, Integer idAddress) {
        // try to update the address
        int row = organisationRepository.updateAddressById(idOrganisation, idAddress);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public void updateOrganisationAddressByName(String name, Integer idAddress) {
        // try to update the address
        int row = organisationRepository.updateAddressByName(name, idAddress);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public void deleteOrganisationById(Integer idOrganisation) throws NoSuchElementException {
        if (! organisationRepository.existsById(idOrganisation))
            throw new NoSuchElementException();
        organisationRepository.deleteById(idOrganisation);
    }

    public void deleteOrganisationByName(String name) throws NoSuchElementException {
        if (! organisationRepository.existsByName(name))
            throw new NoSuchElementException();
        organisationRepository.deleteByName(name);
    }

    private static OrganisationResponseDto modelToResponseDto(Organisation organisation) {
        return OrganisationResponseDto.builder()
                .id(organisation.getId())
                .name(organisation.getName())
                .address(organisation.getAddress())
                .services(organisation.getServices())
                .build();
    }

}
