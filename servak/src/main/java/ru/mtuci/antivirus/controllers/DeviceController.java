package ru.mtuci.antivirus.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.antivirus.entities.requests.DeviceRequest;
import ru.mtuci.antivirus.entities.Device;
import ru.mtuci.antivirus.services.DeviceService;

import java.util.List;
import java.util.Objects;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<?> createDevice(@Valid @RequestBody DeviceRequest deviceRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            String errMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.status(200).body("Ошибка валидации: " + errMsg);
        }
        Device device = deviceService.createDevice(deviceRequest);
        return ResponseEntity.status(200).body(device.toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDevice(@PathVariable Long id){
        Device device = deviceService.getDeviceById(id);
        return ResponseEntity.status(200).body(device.toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceRequest deviceRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            String errMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.status(200).body("Ошибка валидации: " + errMsg);
        }

        Device device = deviceService.updateDevice(id, deviceRequest);
        return ResponseEntity.status(200).body(device.toString());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Long id){
        deviceService.deleteDevice(id);
        return ResponseEntity.status(200).body("Устройство с id: " + id + " успешно удалено");
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices(){
        return ResponseEntity.status(200).body(deviceService.getAllDevices());
    }
}