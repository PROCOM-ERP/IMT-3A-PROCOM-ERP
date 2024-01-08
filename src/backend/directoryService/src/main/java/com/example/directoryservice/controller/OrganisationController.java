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
                .path(Path.ORGANISATION_ID_OR_NAME)
                .buildAndExpand(name)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<OrganisationResponseDto>> getAllOrganisations() {
        return ResponseEntity.ok().body(organisationService.getAllOrganisations());
    }

    @GetMapping(Path.ORGANISATION_ID_OR_NAME)
    public ResponseEntity<OrganisationResponseDto> getOrganisationById(@PathVariable String idOrName) {
        return ResponseEntity.ok().body(organisationService.getOrganisation(idOrName));
    }

    @PatchMapping(Path.ORGANISATION_ID_OR_NAME_ADDRESS)
    public ResponseEntity<String> updateOrganisationAddressById(@PathVariable String idOrName,
                                                                @RequestBody Integer idAddress) {
        organisationService.updateOrganisationAddress(idOrName, idAddress);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(Path.ORGANISATION_ID_OR_NAME)
    public ResponseEntity<String> deleteOrganisationById(@PathVariable String idOrName) {
        organisationService.deleteOrganisation(idOrName);
        return ResponseEntity.noContent().build();
    }

}
