package ru.nand.rbpo2.repositories;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nand.rbpo2.entities.License;

@Repository
public interface LicenseRepository extends JpaRepository<License, Integer> {
}
