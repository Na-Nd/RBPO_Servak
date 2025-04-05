package ru.mtuci.rbposervak.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.rbposervak.entities.Device;
import ru.mtuci.rbposervak.entities.License;
import ru.mtuci.rbposervak.entities.Ticket;
import ru.mtuci.rbposervak.entities.User;
import ru.mtuci.rbposervak.entities.requests.LicenseInfoRequest;
import ru.mtuci.rbposervak.services.DeviceService;
import ru.mtuci.rbposervak.services.LicenseService;
import ru.mtuci.rbposervak.services.UserService;

import java.util.Objects;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseInfoController {

    private final DeviceService deviceService;
    private final LicenseService licenseService;
    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<?> getLicenseInfo(@Valid @RequestBody LicenseInfoRequest licenseInfoRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            String errMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.status(200).body("Ошибка валидации: " + errMsg);
        }

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByLogin(authentication.getName());

            //Device device = deviceService.getDeviceByInfo(licenseInfoRequest.getMacAddress(), user);
            Device device = deviceService.getDeviceByMacAddress(licenseInfoRequest.getMacAddress());

            if(!Objects.equals(device.getUser().getId(), user.getId())){
                throw new IllegalArgumentException("Ошибка аутентификации: неверный пользователь");
            }

            if(device == null){
                return ResponseEntity.status(404).body("Ошибка: устройство не найдено");
            }

            License activeLicense = licenseService.getActiveLicenseForDevice(device, user, licenseInfoRequest.getLicenseCode());

            Ticket ticket = licenseService.generateTicket(activeLicense, device);

            return ResponseEntity.status(200).body("Лицензия найдена, Тикет: " + ticket.toString());

        } catch (Exception e){
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

}
