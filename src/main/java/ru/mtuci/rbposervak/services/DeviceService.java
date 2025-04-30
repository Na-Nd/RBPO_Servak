package ru.mtuci.rbposervak.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbposervak.entities.Device;
import ru.mtuci.rbposervak.entities.User;
import ru.mtuci.rbposervak.entities.requests.ActivationRequest;
import ru.mtuci.rbposervak.entities.requests.DeviceRequest;
import ru.mtuci.rbposervak.repositories.DeviceRepository;
import ru.mtuci.rbposervak.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public Device registerOrUpdateDevice(ActivationRequest activationRequest, User user) {

        Device device = deviceRepository.getDeviceByMacAddress(activationRequest.getMacAddress());
        if (device == null) {
            device = new Device();
            device.setMacAddress(activationRequest.getMacAddress());
            device.setUser(user);
        } else if (!device.getUser().equals(user)) {
            throw new IllegalArgumentException("Device already registered by another user");
        }

        device.setName(activationRequest.getDeviceName());

        return deviceRepository.save(device);
    }

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

    public Device getDeviceByMacAddress(String macAddress) {
        return deviceRepository.findDeviceByMacAddress(macAddress);
    }
}
