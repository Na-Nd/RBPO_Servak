package ru.mtuci.antivirus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.antivirus.entities.LicenseType;

@Repository
public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
    public LicenseType getLicenseTypeById(Long licenseTypeId);
    public LicenseType findLicenseTypeById(Long id);
}
