package ru.mtuci.servak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.servak.entities.License;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
}
