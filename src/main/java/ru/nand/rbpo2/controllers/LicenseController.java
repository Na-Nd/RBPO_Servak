package ru.nand.rbpo2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nand.rbpo2.entities.License;
import ru.nand.rbpo2.seriveces.LicenseService;

@RestController
@RequestMapping("/licenses")
public class LicenseController {

    private final LicenseService licenseService;

    @Autowired
    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addLicense(@RequestBody License license){
        licenseService.save(license);
        return ResponseEntity.ok("Лицензия "+ license.getLicenseCode() + " успешно добавлена");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLicense(@PathVariable("id") int id){
        try {
            licenseService.deleteById(id);
            return ResponseEntity.ok("Лицензия c id " + id + " успешно удалена");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
