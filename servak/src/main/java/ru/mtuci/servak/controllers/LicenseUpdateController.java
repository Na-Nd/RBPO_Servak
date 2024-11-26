package ru.mtuci.servak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.servak.entities.DTO.LicenseUpdateRequest;
import ru.mtuci.servak.entities.Ticket;
import ru.mtuci.servak.services.AuthenticationService;
import ru.mtuci.servak.services.LicenseService;

@RestController
@RequestMapping("/license-update")
public class LicenseUpdateController {
    private final AuthenticationService authenticationService;
    private final LicenseService licenseService;

    @Autowired
    public LicenseUpdateController(AuthenticationService authenticationService, LicenseService licenseService) {
        this.authenticationService = authenticationService;
        this.licenseService = licenseService;
    }

    @PostMapping
    public ResponseEntity<?> renewLicense(@RequestBody LicenseUpdateRequest updateRequest){
        try{
            if(!authenticationService.authenticate(updateRequest.getUsername(), updateRequest.getPassword())){
                return ResponseEntity.status(403).body("Ошибка аутентификации: неверные учетные данные");
            }

            Ticket ticket = licenseService.renewLicense(updateRequest.getLicenseKey(), updateRequest.getUsername());
            if(ticket.getBlocked()){
                return ResponseEntity.status(400).body("Продление лицензии невозможно: " + ticket.getSignature());
            }

            return ResponseEntity.ok(ticket);
        } catch (Exception e){
            return ResponseEntity.status(500).body("Ошибка сервера: " + e.getMessage());
        }
    }
}
