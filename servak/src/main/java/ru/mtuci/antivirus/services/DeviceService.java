package ru.mtuci.antivirus.services;

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
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    public Device registerOrUpdateDevice(ActivationRequest activationRequest, User user) {

        // Получение устройства по MAC-адресу
        Device device = deviceRepository.getDeviceByMacAddress(activationRequest.getMacAddress());
        if (device == null) {
            device = new Device();
            device.setMacAddress(activationRequest.getMacAddress());  // TODO: 1 переделана логика метода
            device.setUser(user);
        } else if (!device.getUser().equals(user)) {
            throw new IllegalArgumentException("Device already registered by another user");
        }

        // Обновление информации об устройстве
        device.setName(activationRequest.getDeviceName());

        return deviceRepository.save(device);
    }

    public Device getDeviceByInfo(String macAddress, User user) {
        return deviceRepository.findDeviceByMacAddressAndUser(macAddress, user);
    }

    /// CRUD operations

    public Device createDevice(DeviceRequest deviceRequest) {
        User user = userRepository.findById(deviceRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Device device = new Device();
        device.setName(deviceRequest.getDeviceName());
        device.setMacAddress(deviceRequest.getMacAddress());
        device.setUser(user);
        return deviceRepository.save(device);
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Device not found"));
    }

    public Device updateDevice(Long id, DeviceRequest deviceRequest) {
        Device device = getDeviceById(id);
        User user = userRepository.findById(deviceRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
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

}
