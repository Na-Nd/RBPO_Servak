package ru.mtuci.servak.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.servak.entities.DTO.ActivationRequest;
import ru.mtuci.servak.entities.Device;
import ru.mtuci.servak.entities.Ticket;
import ru.mtuci.servak.entities.User;
import ru.mtuci.servak.services.DeviceService;
import ru.mtuci.servak.services.LicenseService;
import ru.mtuci.servak.services.UserService;

@RestController
@RequestMapping("/licenses")
public class ActivationController {
    private final DeviceService deviceService;
    private final LicenseService licenseService;
    private final UserService userService;

    @Autowired
    public ActivationController(DeviceService deviceService, LicenseService licenseService, UserService userService) {
        this.deviceService = deviceService;
        this.licenseService = licenseService;
        this.userService = userService;
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@Valid @RequestBody ActivationRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessage.append(error.getField())
                            .append(": ")
                            .append(error.getDefaultMessage())
                            .append("; ")
            );
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(403).body("Пользователь не аутентифицирован");
            }

            String username = authentication.getName();
            System.out.println("В контроллере" + username);

            String activationCode = request.getActivationCode();

            User deviceOwner = userService.findByLogin(username);

            // Регистрируем или обновляем устройство
            Device device = deviceService.registerOrUpdateDevice(request, deviceOwner);

            // Активируем лицензию
            Ticket ticket = licenseService.activateLicense(activationCode, device, username);

            return ResponseEntity.ok(ticket);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            // Общая ошибка
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера: " + ex.getMessage());
        }
    }

    @GetMapping("test")
    public String test() {
        return "success test from act controller";
    }

}
