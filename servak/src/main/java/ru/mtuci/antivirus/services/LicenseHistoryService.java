package ru.mtuci.antivirus.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.antivirus.entities.LicenseHistory;
import ru.mtuci.antivirus.repositories.LicenseHistoryRepository;

@Service
public class LicenseHistoryService {

    private final LicenseHistoryRepository licenseHistoryRepository;

    @Autowired
    public LicenseHistoryService(LicenseHistoryRepository licenseHistoryRepository) {
        this.licenseHistoryRepository = licenseHistoryRepository;
    }

    public void saveLicenseHistory(LicenseHistory licenseHistory) {
        licenseHistoryRepository.save(licenseHistory);
    }

}
