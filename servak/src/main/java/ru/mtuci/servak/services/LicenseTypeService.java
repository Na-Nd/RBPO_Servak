package ru.mtuci.servak.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.servak.entities.LicenseType;
import ru.mtuci.servak.repositories.LicenseTypeRepository;

@Service
public class LicenseTypeService {
    private final LicenseTypeRepository licenseTypeRepository;

    @Autowired
    public LicenseTypeService(LicenseTypeRepository licenseTypeRepository) {
        this.licenseTypeRepository = licenseTypeRepository;
    }

    public LicenseType getLicenseTypeById(Long id){
        LicenseType licenseType = licenseTypeRepository.findById(id).orElse(null);

        if(licenseType == null){
            throw new RuntimeException("Лицензия с id: " + id + " не найдена");
        }

        return licenseType;
    }
}
