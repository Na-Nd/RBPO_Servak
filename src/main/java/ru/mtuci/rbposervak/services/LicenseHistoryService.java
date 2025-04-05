package ru.mtuci.rbposervak.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbposervak.entities.LicenseHistory;
import ru.mtuci.rbposervak.repositories.LicenseHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LicenseHistoryService {

    private final LicenseHistoryRepository licenseHistoryRepository;

    public void saveLicenseHistory(LicenseHistory licenseHistory) {
        licenseHistoryRepository.save(licenseHistory);
    }

    public List<LicenseHistory> getAllLicenseHistories() {
        return licenseHistoryRepository.findAll();
    }

    public LicenseHistory getLicenseHistoryById(Long id) {
        return licenseHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Запись с id " + id + " не найдена"));
    }

    public void deleteLicenseHistoryById(Long id) {
        LicenseHistory licenseHistory = getLicenseHistoryById(id);
        licenseHistoryRepository.delete(licenseHistory);
    }
}
