package ru.mtuci.rbposervak.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.rbposervak.entities.*;
import ru.mtuci.rbposervak.entities.requests.ActivationRequest;
import ru.mtuci.rbposervak.services.*;

import java.util.Objects;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class ActivationController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final LicenseService licenseService;

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@Valid @RequestBody ActivationRequest activationRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            String errMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.status(400).body("Validation error: " + errMsg);
        }

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            User user = userService.findUserByLogin(authentication.getName());

            Device device = deviceService.registerOrUpdateDevice(activationRequest, user);

            String activationCode = activationRequest.getActivationCode();
            String userLogin = user.getLogin();
            Ticket ticket = licenseService.activateLicense(activationCode, device, userLogin);

            return ResponseEntity.status(200).body("License successfully activated: " + ticket.toString());
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body("Validation error: " + e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(500).body(":" + e.getMessage());
        }
    }
}
