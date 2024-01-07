package com.example.directoryservice.controller;

import com.example.directoryservice.dto.OrganisationRequestDto;
import com.example.directoryservice.dto.OrganisationResponseDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(Path.V1_ORGANISATIONS)
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;

    @PostMapping
    public ResponseEntity<String> createOrganisation(@RequestBody OrganisationRequestDto organisationRequestDto) {
        // try to create a new entity
        String name = organisationService.createOrganisation(organisationRequestDto);
        // generate URI location to inform the client how to get information on the new entity
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.ORGANISATION_NAME)
                .buildAndExpand(name)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<OrganisationResponseDto>> getAllOrganisations() {
        return ResponseEntity.ok().body(organisationService.getAllOrganisations());
    }

    @GetMapping(Path.ORGANISATION_ID)
    public ResponseEntity<OrganisationResponseDto> getOrganisationById(@PathVariable Integer idOrganisation) {
        return ResponseEntity.ok().body(organisationService.getOrganisationById(idOrganisation));
    }

    @GetMapping(Path.ORGANISATION_NAME)
    public ResponseEntity<OrganisationResponseDto> getOrganisationByName(@PathVariable String name) {
        return ResponseEntity.ok().body(organisationService.getOrganisationByName(name));
    }

    @PatchMapping(Path.ORGANISATION_ID_ADDRESS)
    public ResponseEntity<String> updateOrganisationAddressById(@PathVariable Integer idOrganisation,
                                                                @RequestBody Integer idAddress) {
        organisationService.updateOrganisationAddressById(idOrganisation, idAddress);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.ORGANISATION_NAME_ADDRESS)
    public ResponseEntity<String> updateOrganisationAddressByName(@PathVariable String name,
                                                                  @RequestBody Integer idAddress) {
        organisationService.updateOrganisationAddressByName(name, idAddress);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(Path.ORGANISATION_ID)
    public ResponseEntity<String> deleteOrganisationById(@PathVariable Integer idOrganisation) {
        organisationService.deleteOrganisationById(idOrganisation);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(Path.ORGANISATION_NAME)
    public ResponseEntity<String> deleteOrganisationByName(@PathVariable String name) {
        organisationService.deleteOrganisationByName(name);
        return ResponseEntity.noContent().build();
    }

}
