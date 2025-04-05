package ru.mtuci.rbposervak.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbposervak.entities.LicenseHistory;
import ru.mtuci.rbposervak.services.LicenseHistoryService;

import java.util.List;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/license-history")
@RequiredArgsConstructor
public class LicenseHistoryController {

    private final LicenseHistoryService licenseHistoryService;

    @GetMapping
    public ResponseEntity<List<LicenseHistory>> getAll() {
        return ResponseEntity.status(200).body(licenseHistoryService.getAllLicenseHistories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLicenseHistoryById(@PathVariable Long id) {
        try{
            return ResponseEntity.status(200).body(licenseHistoryService.getLicenseHistoryById(id));
        } catch (Exception e){
            return ResponseEntity.status(404).body("История с id: " + id + " не найдена");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLicenseHistoryById(@PathVariable Long id){
        try{
            licenseHistoryService.deleteLicenseHistoryById(id);
            return ResponseEntity.status(200).body("История с id: " + id + " удалена");
        } catch (Exception e){
            return ResponseEntity.status(404).body("История с id: " + id + " не найдена");
        }
    }
}
