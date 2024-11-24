package ru.mtuci.servak.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.servak.entities.*;
import ru.mtuci.servak.repositories.LicenseRepository;

import java.util.Date;
import java.util.Optional;

@Service
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final ProductService productService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final LicenseHistoryService licenseHistoryService;

    @Autowired
    public LicenseService(LicenseRepository licenseRepository, ProductService productService, UserService userService, LicenseTypeService licenseTypeService, LicenseHistoryService licenseHistoryService) {
        this.licenseRepository = licenseRepository;
        this.productService = productService;
        this.userService = userService;
        this.licenseTypeService = licenseTypeService;
        this.licenseHistoryService = licenseHistoryService;
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
}
