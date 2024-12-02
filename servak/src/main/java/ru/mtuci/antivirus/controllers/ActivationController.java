package ru.mtuci.antivirus.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.antivirus.entities.requests.ActivationRequest;
import ru.mtuci.antivirus.entities.*;
import ru.mtuci.antivirus.services.*;

@RestController
@RequestMapping("/license")
public class ActivationController {

    private final UserService userService;
    private final DeviceService deviceService;
    private final LicenseService licenseService;

    @Autowired
    public ActivationController(UserService userService, DeviceService deviceService, LicenseService licenseService) {
        this.userService = userService;
        this.deviceService = deviceService;
        this.licenseService = licenseService;
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@Valid @RequestBody ActivationRequest activationRequest/*, BindingResult bindingResult*/) {

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(403).body("User is not authenticated");
            }

            User user = userService.findUserByLogin(authentication.getName());

            Device device = deviceService.registerOrUpdateDevice(activationRequest, user);

            String activationCode = activationRequest.getActivationCode();
            String login = user.getLogin();
            Ticket ticket = licenseService.activateLicense(activationCode, device, login);

            return ResponseEntity.ok("License activated successfully. Ticket:\n" + ticket.getBody());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }

    }
}
