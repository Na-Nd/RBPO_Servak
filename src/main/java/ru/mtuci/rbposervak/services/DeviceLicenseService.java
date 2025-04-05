package ru.mtuci.rbposervak.services;

import org.springframework.stereotype.Service;
import ru.mtuci.rbposervak.entities.DeviceLicense;
import ru.mtuci.rbposervak.entities.License;
import ru.mtuci.rbposervak.repositories.DeviceLicenseRepository;

import java.util.List;

@Service
public class DeviceLicenseService {

    private final DeviceLicenseRepository deviceLicenseRepository;

    public DeviceLicenseService(DeviceLicenseRepository deviceLicenseRepository) {
        this.deviceLicenseRepository = deviceLicenseRepository;
    }

    public List<DeviceLicense> getDeviceLicensesByLicense(License license) {
        return deviceLicenseRepository.getDeviceLicensesByLicense(license);
    }

    public void save(DeviceLicense deviceLicense) {
        deviceLicenseRepository.save(deviceLicense);
    }

    public DeviceLicense getDeviceLicenseByDeviceIdAndLicenseId(Long deviceId, Long licenseId) {
        return deviceLicenseRepository.getDeviceLicenseByDeviceIdAndLicenseId(deviceId, licenseId);
    }
}
