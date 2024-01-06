package com.example.directoryservice.controller;

import com.example.directoryservice.dto.ServiceRequestDto;
import com.example.directoryservice.dto.ServiceResponseDto;
import com.example.directoryservice.model.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Path.V1_SERVICES)
public class ServiceController {

    @PostMapping
    public ResponseEntity<String> createService(@RequestBody ServiceRequestDto serviceRequestDto) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> getAllServices() {
        // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping(Path.SERVICE_ID)
    public ResponseEntity<ServiceResponseDto> getService(@PathVariable String idService) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @PatchMapping(Path.SERVICE_ID_ADDRESS)
    public ResponseEntity<String> updateServiceAddress(@PathVariable String idService,
                                                       @RequestBody Long idAddress) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @PatchMapping(Path.SERVICE_ID_ORGANISATION)
    public ResponseEntity<String> updateServiceOrganisation(@PathVariable String idService,
                                                            @RequestBody Long idOrganisation) {
        // TODO
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(Path.SERVICE_ID)
    public ResponseEntity<String> deleteService(@PathVariable String idService) {
        // TODO
        return ResponseEntity.ok().build();
    }

}
