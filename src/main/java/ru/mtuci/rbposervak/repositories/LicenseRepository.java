package ru.mtuci.rbposervak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbposervak.entities.*;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    License getLicensesByCode(String code);
    License getLicenseById(Long id);
}
