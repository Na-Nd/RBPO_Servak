package ru.mtuci.servak.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.servak.entities.DeviceLicense;
import ru.mtuci.servak.repositories.DeviceLicenseRepository;

@Service
public class DeviceLicenseService {
    private final DeviceLicenseRepository deviceLicenseRepository;

    @Autowired
    public DeviceLicenseService(DeviceLicenseRepository deviceLicenseRepository) {
        this.deviceLicenseRepository = deviceLicenseRepository;
    }

    public void save(DeviceLicense deviceLicense) {
        deviceLicenseRepository.save(deviceLicense);
    }
}
