package ru.mtuci.rbpo2.seriveces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo2.repositories.LicenseRepository;
import ru.mtuci.rbpo2.entities.License;

import java.util.Optional;

@Service
public class LicenseService {

    private final LicenseRepository licenseRepository;

    @Autowired
    public LicenseService(LicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
    }

    public void save(License license){
        licenseRepository.save(license);
    }

    public Optional<License> findLicenseById(int id){
        return licenseRepository.findById(id);
    }

    public void deleteById(int id) {
        if (licenseRepository.existsById(id)) {
            licenseRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("License with id " + id + " not found");
        }
    }
}