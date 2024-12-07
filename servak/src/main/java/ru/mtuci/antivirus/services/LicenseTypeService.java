package ru.mtuci.antivirus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.antivirus.entities.requests.LicenseTypeRequest;
import ru.mtuci.antivirus.entities.LicenseType;
import ru.mtuci.antivirus.repositories.LicenseTypeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LicenseTypeService {

    private final LicenseTypeRepository licenseTypeRepository;

    public LicenseType getLicenseTypeById(Long licenseTypeId) {
        return licenseTypeRepository.getLicenseTypeById(licenseTypeId);
    }

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
