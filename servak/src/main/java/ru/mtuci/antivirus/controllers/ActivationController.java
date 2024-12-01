package ru.mtuci.antivirus.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.antivirus.entities.DTO.ActivationRequest;
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


    @GetMapping("/test")
    public String test() {
        return "ActivationController: Tested successfully";
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@Valid @RequestBody ActivationRequest activationRequest/*, BindingResult bindingResult*/) {

        // TODO: (if needed) check bindingResult for validation errors (@NotBlank, etc.)

        System.out.println("ActivationController: activateLicense: Started activating license, data: " + activationRequest.getActivationCode() + ", " + activationRequest.getDeviceName() + ", " + activationRequest.getMacAddress());

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(403).body("User is not authenticated");
            }

            // Get authenticated user
            System.out.println("ActivationController: activateLicense: Request from user: " + authentication.getName());
            User user = userService.findUserByLogin(authentication.getName());

            // Register or update device
            Device device = deviceService.registerOrUpdateDevice(activationRequest, user);

            // Activate license
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
