package ru.mtuci.servak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.servak.entities.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    public Device findByMacAddress(String macAddress);
}
