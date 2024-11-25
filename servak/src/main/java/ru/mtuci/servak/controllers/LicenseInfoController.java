package ru.mtuci.servak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.servak.entities.Device;
import ru.mtuci.servak.entities.License;
import ru.mtuci.servak.entities.Ticket;
import ru.mtuci.servak.entities.User;
import ru.mtuci.servak.services.DeviceService;
import ru.mtuci.servak.services.LicenseService;
import ru.mtuci.servak.services.UserService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/license-info")
public class LicenseInfoController {
    private final DeviceService deviceService;
    private final LicenseService licenseService;
    private final UserService userService;

    @Autowired
    public LicenseInfoController(DeviceService deviceService, LicenseService licenseService, UserService userService) {
        this.deviceService = deviceService;
        this.licenseService = licenseService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getLicenseInfo(@RequestParam("macAddress") String macAddress) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(403).body("Пользователь не аутентифицирован");
            }

            String username = authentication.getName();
            User user = userService.findByLogin(username);

            Device device = deviceService.findDeviceByInfo(macAddress, user);
            if (device == null) {
                return ResponseEntity.status(400).body("Устройство не найдено");
            }

            // Получение незаблокированных лицензий
            List<License> licenses = licenseService.getActiveLicensesForDevice(device, user);
            System.out.println("Полученные лицензии для устройства: " + device.getMacAddress());
            licenses.forEach(System.out::println);

            if (licenses.isEmpty()) {
                return ResponseEntity.status(400).body("Нет активных лицензий для устройства");
            }

            Ticket ticket = generateTicket(licenses.getFirst(), device);

            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка сервера: " + e.getMessage());
        }
    }

    private Ticket generateTicket(License license, Device device) {
        Ticket ticket = new Ticket();
        ticket.setCurrentDate(new Date());
        ticket.setLivingTime(license.getDuration());
        ticket.setActivationDate(new Date());
        ticket.setExpirationDate(license.getEndingDate());
        ticket.setUserId(license.getUser().getId().intValue());
        ticket.setDeviceId(device.getId().intValue());
        ticket.setBlocked(false);
        ticket.setSignature(generateSignature(ticket));

        return ticket;
    }

    private String generateSignature(Ticket ticket) {
        return "bks2202_signature"; // Как в LicenseService
    }
}
