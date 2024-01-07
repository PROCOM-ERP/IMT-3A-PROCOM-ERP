package com.example.directoryservice.controller;

import com.example.directoryservice.dto.ServiceRequestDto;
import com.example.directoryservice.dto.ServiceResponseDto;
import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(Path.V1_SERVICES)
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<String> createService(@RequestBody ServiceRequestDto serviceRequestDto) {
        // try to create a new entity
        Integer idService = serviceService.createService(serviceRequestDto);
        // generate URI location to inform the client how to get information on the new entity
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.SERVICE_ID)
                .buildAndExpand(idService)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> getAllServices() {
        return ResponseEntity.ok().body(serviceService.getAllServices());
    }

    @GetMapping(Path.SERVICE_ID)
    public ResponseEntity<ServiceResponseDto> getService(@PathVariable Integer idService) {
        return ResponseEntity.ok().body(serviceService.getService(idService));
    }

    @PatchMapping(Path.SERVICE_ID_ADDRESS)
    public ResponseEntity<String> updateServiceAddress(@PathVariable Integer idService,
                                                       @RequestBody Integer idAddress) {
        serviceService.updateServiceAddress(idService, idAddress);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(Path.SERVICE_ID_ORGANISATION)
    public ResponseEntity<String> updateServiceOrganisation(@PathVariable Integer idService,
                                                            @RequestBody Integer idOrganisation) {
        serviceService.updateServiceOrganisation(idService, idOrganisation);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(Path.SERVICE_ID)
    public ResponseEntity<String> deleteService(@PathVariable Integer idService) {
        serviceService.deleteServiceById(idService);
        return ResponseEntity.noContent().build();
    }

}
