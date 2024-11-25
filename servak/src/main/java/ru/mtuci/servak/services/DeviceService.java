package ru.mtuci.servak.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.servak.entities.DTO.ActivationRequest;
import ru.mtuci.servak.entities.Device;
import ru.mtuci.servak.entities.User;
import ru.mtuci.servak.repositories.DeviceRepository;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserService userService;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserService userService) {
        this.deviceRepository = deviceRepository;
        this.userService = userService;
    }

    public Device registerOrUpdateDevice(ActivationRequest activationRequest, User deviceOwner) {
        // Поиск устройства по MAC-адресу
        Device device = deviceRepository.findByMacAddress(activationRequest.getMacAddress());
        if (device == null) {
            device = new Device();
        }

        // Обновление информации об устройстве
        device.setName(activationRequest.getDeviceName());
        device.setMacAddress(activationRequest.getMacAddress());

        device.setUser(deviceOwner);

        return deviceRepository.save(device);
    }

    public Device findDeviceByInfo(String macAddress, User user) {
        return deviceRepository.findDeviceByMacAddressAndUser(macAddress, user);
    }
}
