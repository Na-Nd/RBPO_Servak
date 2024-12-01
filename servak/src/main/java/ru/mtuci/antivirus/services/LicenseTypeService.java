package ru.mtuci.antivirus.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.antivirus.entities.DTO.LicenseTypeRequest;
import ru.mtuci.antivirus.entities.LicenseType;
import ru.mtuci.antivirus.repositories.LicenseTypeRepository;

import java.util.List;

@Service
public class LicenseTypeService {

    private final LicenseTypeRepository licenseTypeRepository;

    @Autowired
    public LicenseTypeService(LicenseTypeRepository licenseTypeRepository) {
        this.licenseTypeRepository = licenseTypeRepository;
    }

    public LicenseType getLicenseTypeById(Long licenseTypeId) {
        return licenseTypeRepository.getLicenseTypeById(licenseTypeId);
    }

    /// CRUD operations

    public LicenseType createLicenseType(LicenseTypeRequest licenseTypeRequest) {
        LicenseType licenseType = new LicenseType();
        licenseType.setName(licenseTypeRequest.getName());
        licenseType.setDefaultDuration(licenseTypeRequest.getDefaultDuration());
        licenseType.setDescription(licenseTypeRequest.getDescription());
        return licenseTypeRepository.save(licenseType);
    }

    public LicenseType updateLicenseType(Long id, LicenseTypeRequest licenseTypeRequest) {
        LicenseType licenseType = licenseTypeRepository.findLicenseTypeById(id);
        if (licenseType != null) {
            licenseType.setName(licenseTypeRequest.getName());
            licenseType.setDefaultDuration(licenseTypeRequest.getDefaultDuration());
            licenseType.setDescription(licenseTypeRequest.getDescription());
            return licenseTypeRepository.save(licenseType);
        }
        return null;
    }

    public void deleteLicenseType(Long id) {
        licenseTypeRepository.deleteById(id);
    }

    public List<LicenseType> getAllLicenseTypes() {
        return licenseTypeRepository.findAll();
    }

}
