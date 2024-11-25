package ru.mtuci.servak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.servak.entities.DeviceLicense;

@Repository
public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
}
