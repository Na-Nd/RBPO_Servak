package ru.mtuci.antivirus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.antivirus.entities.requests.ActivationRequest;
import ru.mtuci.antivirus.entities.requests.DeviceRequest;
import ru.mtuci.antivirus.entities.Device;
import ru.mtuci.antivirus.entities.User;
import ru.mtuci.antivirus.repositories.DeviceRepository;
import ru.mtuci.antivirus.repositories.UserRepository;

import java.util.List;

//TODO: 1. Пересмотреть логику обновления пользователя устройства ✅

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public Device registerOrUpdateDevice(ActivationRequest activationRequest, User user) {

        Device device = deviceRepository.getDeviceByMacAddress(activationRequest.getMacAddress());
        if (device == null) {
            device = new Device();
            device.setMacAddress(activationRequest.getMacAddress());  // TODO: 1 переделана логика метода
            device.setUser(user);
        } else if (!device.getUser().equals(user)) {
            throw new IllegalArgumentException("Устройство зарегистрировано другим пользователем");
        }

        device.setName(activationRequest.getDeviceName());

        return deviceRepository.save(device);
    }

    public Device getDeviceByInfo(String macAddress, User user) {
        return deviceRepository.findDeviceByMacAddressAndUser(macAddress, user);
    }

    public Device createDevice(DeviceRequest deviceRequest) {
        User user = userRepository.findById(deviceRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        Device device = new Device();
        device.setName(deviceRequest.getDeviceName());
        device.setMacAddress(deviceRequest.getMacAddress());
        device.setUser(user);
        return deviceRepository.save(device);
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Устройство не найдено"));
    }

    public Device updateDevice(Long id, DeviceRequest deviceRequest) {
        Device device = getDeviceById(id);
        User user = userRepository.findById(deviceRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        device.setName(deviceRequest.getDeviceName());
        device.setMacAddress(deviceRequest.getMacAddress());
        device.setUser(user);
        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDeviceByMacAddress(String macAddress) {
        return deviceRepository.findDeviceByMacAddress(macAddress);
    }
}
