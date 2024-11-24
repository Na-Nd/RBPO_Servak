package ru.mtuci.servak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.servak.entities.DTO.LicenseRequest;
import ru.mtuci.servak.entities.License;
import ru.mtuci.servak.services.LicenseService;

@RestController
@RequestMapping("/license")
public class LicenseController {
    private final LicenseService licenseService;

    @Autowired
    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createLicense(@RequestBody LicenseRequest licenseRequest) {
        System.out.println("Попал в createLicense");
        try{
            License license = licenseService.createLicense(
                    licenseRequest.getProductId(),
                    licenseRequest.getOwnerId(),
                    licenseRequest.getLicenseTypeId(),
                    licenseRequest.getCode(),
                    licenseRequest.getDescription(),
                    licenseRequest.getDeviceCount(),
                    licenseRequest.getDuration()
            );
            return ResponseEntity.ok(license);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
