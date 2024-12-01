package ru.mtuci.antivirus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.mtuci.antivirus.entities.LicenseHistory;

@Repository
public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
    LicenseHistory findLicenseHistoryById(Long id);
}
