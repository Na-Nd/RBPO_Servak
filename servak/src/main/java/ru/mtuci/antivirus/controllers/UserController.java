package ru.mtuci.antivirus.controllers;

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
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/info/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id){
        User user = userService.getUserById(id);
        return ResponseEntity.status(200).body("Пользователь: " + user.toString());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/info")
    public ResponseEntity<List<User>> getUserInfo(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity.status(200).body(users);
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserRequest user, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String findUsername = userDetails.getUsername();

            User currentUser = userService.findUserByLogin(findUsername);

            if (user.getLogin() != null) {
                currentUser.setLogin(user.getLogin());
            }
            if (user.getEmail() != null) {
                currentUser.setEmail(user.getEmail());
            }
            if (user.getPasswordHash() != null) {
                currentUser.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }

            userService.saveUser(currentUser);

            return ResponseEntity.status(200).body("Польователь: " + currentUser.getLogin() + " успешно обновлен");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при обновлении пользователя");
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        try{
            User user = userService.getUserById(id);

            userService.deleteUser(id);
            return ResponseEntity.status(200).body("Пользователь с id: " + id + " удален");
        } catch (Exception e){
            return ResponseEntity.status(500).body("Ошибка при удалении пользователя");
        }
    }
}
