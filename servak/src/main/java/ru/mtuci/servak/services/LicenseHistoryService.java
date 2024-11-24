package ru.mtuci.servak.services;

import org.springframework.stereotype.Service;
import ru.mtuci.servak.entities.License;
import ru.mtuci.servak.entities.LicenseHistory;
import ru.mtuci.servak.entities.User;
import ru.mtuci.servak.repositories.LicenseHistoryRepository;

import java.util.Date;

@Service
public class LicenseHistoryService {

    private final LicenseHistoryRepository licenseHistoryRepository;

    public LicenseHistoryService(LicenseHistoryRepository licenseHistoryRepository) {
        this.licenseHistoryRepository = licenseHistoryRepository;
    }

    public void recordLicenseChange(License license, User owner, String status, String description) {
        LicenseHistory history = new LicenseHistory();
        history.setLicense(license);
        history.setUser(owner);
        history.setStatus(status);
        history.setDescription(description);
        history.setChangeDate(new Date());
        licenseHistoryRepository.save(history);
    }
}
