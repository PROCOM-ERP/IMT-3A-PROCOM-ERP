package com.example.directoryservice.service;

import com.example.directoryservice.dto.OrganisationRequestDto;
import com.example.directoryservice.dto.OrganisationResponseDto;
import com.example.directoryservice.model.Organisation;
import com.example.directoryservice.repository.AddressRepository;
import com.example.directoryservice.repository.OrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final AddressRepository addressRepository;

    //private final Logger logger = LoggerFactory.getLogger(OrganisationService.class);

    @Transactional
    public String createOrganisation(OrganisationRequestDto organisationRequestDto)
            throws DataIntegrityViolationException {
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

    public OrganisationResponseDto getOrganisation(String idOrName) throws NoSuchElementException {

        Optional<Organisation> organisation = StringUtils.isNumeric(idOrName) ?
                getOrganisationById(Integer.parseInt(idOrName)) :
                getOrganisationByName(idOrName);
        return organisation
                .map(OrganisationService::modelToResponseDto)
                .orElseThrow();
    }

    public Optional<Organisation> getOrganisationById(Integer idOrganisation){
        return organisationRepository.findById(idOrganisation);
    }

    public Optional<Organisation> getOrganisationByName(String name) {
        return organisationRepository.findByName(name);
    }

    public void updateOrganisationAddress(String idOrName, Integer idAddress)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update the address
        int row = StringUtils.isNumeric(idOrName) ?
                updateOrganisationAddressById(Integer.parseInt(idOrName), idAddress) :
                updateOrganisationAddressByName(idOrName, idAddress);

        // check if only 1 row was modified
        if (row != 1) {
            throw new NoSuchElementException();
        }
    }

    public int updateOrganisationAddressById(Integer idOrganisation, Integer idAddress)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update the address
        return organisationRepository.updateAddressById(idOrganisation, idAddress);
    }

    public int updateOrganisationAddressByName(String name, Integer idAddress)
            throws NoSuchElementException, DataIntegrityViolationException {
        // try to update the address
        return organisationRepository.updateAddressByName(name, idAddress);
    }

    public void deleteOrganisation(String idOrName) throws NoSuchElementException {
        if(StringUtils.isNumeric(idOrName))
            deleteOrganisationById(Integer.parseInt(idOrName));
        else
            deleteOrganisationByName(idOrName);
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
