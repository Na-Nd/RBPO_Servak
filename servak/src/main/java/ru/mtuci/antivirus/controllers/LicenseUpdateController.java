package ru.mtuci.antivirus.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.antivirus.entities.requests.LicenseUpdateRequest;
import ru.mtuci.antivirus.entities.Ticket;
import ru.mtuci.antivirus.services.LicenseService;

import java.util.Objects;

//TODO: 1. Убрать лишние проверки ✅

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseUpdateController {

    private final LicenseService licenseService;

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody LicenseUpdateRequest licenseUpdateRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            String errMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.status(200).body("Ошибка валидации: " + errMsg);
        }

        try{
            Ticket ticket = licenseService.updateExistentLicense(
                    licenseUpdateRequest.getLicenseCode(),
                    licenseUpdateRequest.getLogin(),
                    licenseUpdateRequest.getMacAddress()
            );

            return ResponseEntity.status(200).body("Успешное обновление лицензии, тикет: " + ticket.toString());
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
