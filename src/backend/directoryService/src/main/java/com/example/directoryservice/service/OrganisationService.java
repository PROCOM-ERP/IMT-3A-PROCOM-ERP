package com.example.directoryservice.service;

import com.example.directoryservice.dto.OrgUnitResponseDto;
import com.example.directoryservice.dto.OrganisationResponseDto;
import com.example.directoryservice.model.Organisation;
import com.example.directoryservice.repository.OrganisationRepository;
import lombok.RequiredArgsConstructor;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private final OrganisationRepository organisationRepository;

    //private final Logger logger = LoggerFactory.getLogger(OrganisationService.class);

    public Set<OrganisationResponseDto> getAllOrganisations() {
        return organisationRepository.findAll().stream()
                .map(this::modelToResponseDto)
                .collect(Collectors.toSet());
    }

    private OrganisationResponseDto modelToResponseDto(Organisation organisation) {
        return OrganisationResponseDto.builder()
                .id(organisation.getId())
                .name(organisation.getName())
                .orgUnits(organisation.getOrgUnits().stream()
                        .map(ou -> OrgUnitResponseDto.builder()
                                .id(ou.getId())
                                .name(ou.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

}
