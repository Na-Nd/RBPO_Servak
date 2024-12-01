package ru.mtuci.antivirus.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.antivirus.entities.Device;
import ru.mtuci.antivirus.entities.User;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device getDeviceByMacAddress(String macAddress);
    Device findDeviceByMacAddressAndUser(String macAddress, User user);
    Device findDeviceByMacAddress(String macAddress);
    Device findDeviceByUser(User user);
}
