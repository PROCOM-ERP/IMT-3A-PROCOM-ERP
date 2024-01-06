package com.example.directoryservice.controller;

import com.example.directoryservice.dto.OrganisationRequestDto;
import com.example.directoryservice.dto.OrganisationResponseDto;
import com.example.directoryservice.model.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Path.V1_ORGANISATIONS)
public class OrganisationController {

    @PostMapping
    public ResponseEntity<String> createOrganisation(@RequestBody OrganisationRequestDto organisationRequestDto) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OrganisationResponseDto>> getAllOrganisations() {
        // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping(Path.ORGANISATION_ID)
    public ResponseEntity<OrganisationResponseDto> getOrganisationById(@PathVariable String idOrganisation) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping(Path.ORGANISATION_NAME)
    public ResponseEntity<OrganisationResponseDto> getOrganisationByName(@PathVariable String name) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @PatchMapping(Path.ORGANISATION_ID_ADDRESS)
    public ResponseEntity<String> updateOrganisationAddressById(@PathVariable String idOrganisation,
                                                                @RequestBody Long idAddress) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @PatchMapping(Path.ORGANISATION_NAME_ADDRESS)
    public ResponseEntity<String> updateOrganisationAddressByName(@PathVariable String name,
                                                                  @RequestBody Long idAddress) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(Path.ORGANISATION_ID)
    public ResponseEntity<String> deleteOrganisationById(@PathVariable String idOrganisation) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(Path.ORGANISATION_NAME)
    public ResponseEntity<String> deleteOrganisationByName(@PathVariable String name) {
        // TODO
        return ResponseEntity.ok().build();
    }

}
