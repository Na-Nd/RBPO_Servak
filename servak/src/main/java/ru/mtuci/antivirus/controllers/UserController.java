package ru.mtuci.antivirus.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.antivirus.entities.requests.UserRequest;
import ru.mtuci.antivirus.entities.User;
import ru.mtuci.antivirus.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/info/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id){
        return ResponseEntity.status(200).body("Пользователь: " + userService.getUserById(id).toString());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/info")
    public ResponseEntity<List<User>> getUserInfo(){
        return ResponseEntity.status(200).body(userService.getAllUsers());
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserRequest user, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String findUsername = userDetails.getUsername();
            User currentUser = userService.findUserByLogin(findUsername);

            if (user.getLogin() != null && !user.getLogin().equals(currentUser.getLogin())) {
                if (userService.existsByLogin(user.getLogin())) {
                    return ResponseEntity.status(400).body("Ошибка валидации: такой логин уже существует");
                }

                currentUser.setLogin(user.getLogin());
            }

            if (user.getEmail() != null && !user.getEmail().equals(currentUser.getEmail())) {
                if (userService.existsByEmail(user.getEmail())) {
                    return ResponseEntity.status(400).body("Ошибка валидации: такая почта уже существует");
                }
                currentUser.setEmail(user.getEmail());
            }

            if (user.getPasswordHash() != null) {
                currentUser.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }

            userService.saveUser(currentUser);

            return ResponseEntity.status(200).body("Данные пользователя " + currentUser.getLogin() + " обновлены");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        try{
            userService.deleteUser(id);
            return ResponseEntity.status(200).body("Пользователь с id: " + id + " удален");
        } catch (Exception e){
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
