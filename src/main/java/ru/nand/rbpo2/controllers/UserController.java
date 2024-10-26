package ru.nand.rbpo2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nand.rbpo2.entities.DTO.UserDTO;
import ru.nand.rbpo2.entities.License;
import ru.nand.rbpo2.entities.User;
import ru.nand.rbpo2.entities.enums.ROLE;
import ru.nand.rbpo2.seriveces.LicenseService;
import ru.nand.rbpo2.seriveces.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final LicenseService licenseService;

    @Autowired
    public UserController(UserService userService, LicenseService licenseService) {
        this.userService = userService;
        this.licenseService = licenseService;
    }

    @GetMapping("/get")
    public List<UserDTO> getAllUsers() {
        return userService.findAll().stream() // Вернем DTO чтоб не палить пароли
                .map(user -> new UserDTO(user.getId(), user.getLogin(), user.getRole(), user.getLicenses()))
                .collect(Collectors.toList());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        user.setRole(ROLE.USER);
        userService.save(user);
        return ResponseEntity.ok("Пользователь добавлен успешно");
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") int id) {
        return userService.findById(id) // тоже вернем DTO чтоб не палить пароли
                .map(user -> ResponseEntity.ok().body(new UserDTO(user.getId(), user.getLogin(), user.getRole(), user.getLicenses())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/addLicense/{user_id}/{license_id}")
    public ResponseEntity<?> addLicenseForUser(@PathVariable("user_id") int userId, @PathVariable("license_id") int licenseId) {
        try {
            userService.addLicenseForUser(userId, licenseId);
            Optional<User> user = userService.findById(userId);
            Optional<License> license = licenseService.findLicenseById(licenseId);

            if (user.isPresent() && license.isPresent()) {
                User currentUser = user.get();
                License currentLicense = license.get();
                return ResponseEntity.ok("Лицензия " + currentLicense.getLicenseCode() + " успешно добавлена для пользователя " + currentUser.getLogin());
            }

            return ResponseEntity.badRequest().body("Неверные поля");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok("Пользователь с id: " + id + " успешно удален");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
