package ru.mtuci.antivirus.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.antivirus.entities.DTO.LicenseTypeRequest;
import ru.mtuci.antivirus.entities.LicenseType;
import ru.mtuci.antivirus.services.LicenseTypeService;

import java.util.List;

@RestController
@RequestMapping("/license-types")
public class LicenseTypeController {

    private final LicenseTypeService licenseTypeService;

    @Autowired
    public LicenseTypeController(LicenseTypeService licenseTypeService) {
        this.licenseTypeService = licenseTypeService;
    }

    @PostMapping
    public ResponseEntity<?> createLicenseType(@Valid @RequestBody LicenseTypeRequest licenseTypeRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        LicenseType licenseType = licenseTypeService.createLicenseType(licenseTypeRequest);
        return ResponseEntity.ok(licenseType);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLicenseType(@PathVariable Long id) {
        LicenseType licenseType = licenseTypeService.getLicenseTypeById(id);
        return ResponseEntity.ok(licenseType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLicenseType(@PathVariable Long id, @Valid @RequestBody LicenseTypeRequest licenseTypeRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        LicenseType licenseType = licenseTypeService.updateLicenseType(id, licenseTypeRequest);
        return ResponseEntity.ok(licenseType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicenseType(@PathVariable Long id) {
        licenseTypeService.deleteLicenseType(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<LicenseType>> getAllLicenseTypes() {
        List<LicenseType> licenseTypes = licenseTypeService.getAllLicenseTypes();
        return ResponseEntity.ok(licenseTypes);
    }
}