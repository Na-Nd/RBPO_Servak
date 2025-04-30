package ru.mtuci.rbposervak.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.rbposervak.entities.*;
import ru.mtuci.rbposervak.entities.requests.LicenseRequest;
import ru.mtuci.rbposervak.services.LicenseService;

import java.util.Objects;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createLicense(@Valid @RequestBody LicenseRequest licenseRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            String errMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.status(200).body("Validation error: " + errMsg);
        }

        try{
            License license = licenseService.createLicense(licenseRequest);

            if(license == null){
                return ResponseEntity.status(401).body("Error creating license");
            }

            return ResponseEntity.status(200).body("License created successfully. " + license);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body("Validation error: " + e.getMessage());
        }
    }
}