package ru.mtuci.servak.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.servak.entities.*;
import ru.mtuci.servak.repositories.LicenseRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final ProductService productService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceLicenseService deviceLicenseService;

    @Autowired
    public LicenseService(LicenseRepository licenseRepository, ProductService productService, UserService userService, LicenseTypeService licenseTypeService, LicenseHistoryService licenseHistoryService, DeviceLicenseService deviceLicenseService) {
        this.licenseRepository = licenseRepository;
        this.productService = productService;
        this.userService = userService;
        this.licenseTypeService = licenseTypeService;
        this.licenseHistoryService = licenseHistoryService;
        this.deviceLicenseService = deviceLicenseService;
    }

    public License createLicense(
            Long productId,
            Long ownerId,
            Long licenseTypeId,
            String code,
            String description,
            Integer deviceCount,
            Integer duration
    ){
        Product product = productService.getProductById(productId);
        if(product == null) {
            throw new IllegalArgumentException("Продукт не найден");
        }

        User owner = userService.getUserById(ownerId);
        if(owner == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        LicenseType licenseType = licenseTypeService.getLicenseTypeById(licenseTypeId);
        if (licenseType == null){
            throw new IllegalArgumentException("Тип лицензии не найден");
        }

        // Создаем лицензию
        License license = new License(product,owner,licenseType,code,description,deviceCount,duration);

        // Рассчет даты окончания
        if(duration != null){
            Date endingDate = new Date(System.currentTimeMillis() + duration * 24L * 60 * 60 * 1000); // TODO
            license.setEndingDate(endingDate);
        }

        license.setFirstActivationDate(new Date());
        license.setBlocked(false);

        licenseRepository.save(license); // Сохраняем

        // Запись в историю изменений
        licenseHistoryService.recordLicenseChange(license, owner, "Создана", description);

        return license;
    }

    public Ticket activateLicense(String activationCode, Device device, String username) {
        License license = findLicenseByCode(activationCode);
        if (license == null) {
            throw new IllegalArgumentException("Лицензия не найдена");
        }

        // Валидация
        validateActivation(license, device, username);

        // Привязка
        createDeviceLicense(license, device);

        // Обновление лицензии
        updateLicense(license);

        User currentUser = userService.findByLogin(username);

        // Запись в историю
        licenseHistoryService.recordLicenseChange(license, currentUser, "Activated", "Лицензия активирована");

        // Генерация тикета
        return generateTicket(license, device);
    }

    private License findLicenseByCode(String code) {
        return licenseRepository.findByCode(code);
    }

    private void validateActivation(License license, Device device, String username) {
        if (license.getBlocked()) {
            throw new IllegalArgumentException("Активация невозможна: лицензия заблокирована");
        }

        if (license.getEndingDate().before(new Date())) {
            throw new IllegalArgumentException("Активация невозможна: лицензия истекла");
        }

        // TODO Дополнительные проверки
    }

    private void createDeviceLicense(License license, Device device) {
        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setDevice(device);
        deviceLicense.setLicense(license);
        deviceLicense.setActivationDate(new Date());

        deviceLicenseService.save(deviceLicense);
    }

    private void updateLicense(License license) {
        licenseRepository.save(license);
    }

    private Ticket generateTicket(License license, Device device) {
        Ticket ticket = new Ticket();
        ticket.setCurrentDate(new Date());
        ticket.setLivingTime(license.getDuration());
        ticket.setActivationDate(new Date());
        ticket.setExpirationDate(license.getEndingDate());
        ticket.setUserId(license.getUser().getId().intValue());
        ticket.setDeviceId(device.getId().intValue());
        ticket.setBlocked(false);
        ticket.setSignature(generateSignature(ticket));

        return ticket;
    }

    private String generateSignature(Ticket ticket) {
        return "bks2202_signature"; // TODO Не понял что за подпись должна быть
    }

    // Получение незаблокированных лицензий
    public List<License> getActiveLicensesForDevice(Device device, User user){
        return device.getDeviceLicenses().stream()
                .map(DeviceLicense::getLicense)
                .filter(license -> !license.getBlocked())
                .toList();
    }
}
