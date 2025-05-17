package ru.mtuci.rbposervak.controllers;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbposervak.entities.DTO.TokenResponse;
import ru.mtuci.rbposervak.entities.DTO.UserLoginDTO;
import ru.mtuci.rbposervak.entities.DTO.UserRegisterDTO;
import ru.mtuci.rbposervak.entities.ENUMS.ROLE;
import ru.mtuci.rbposervak.entities.User;
import ru.mtuci.rbposervak.entities.UserSession;
import ru.mtuci.rbposervak.services.UserService;
import ru.mtuci.rbposervak.utils.JwtUtil;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello World!";
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserRegisterDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.badRequest().body("Validation error: " + errMsg);
        }

        try{
            // Регистрация и создание сессии
            UserSession session = userService.registerUser(userDTO);

            // Рефреш возвращать не будем
            return ResponseEntity.status(200).body(new TokenResponse(session.getAccessToken(), null));
        } catch (Exception e){
            System.out.println("Ошибка при регистрации пользователя: " + e.getMessage());
            return ResponseEntity.status(400).body(
                    new TokenResponse(null, null, "Registration error: " + e.getMessage())
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserLoginDTO userDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            String errMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.status(200).body("Validation error: " + errMsg);
        }

        try{
            UserSession session = userService.loginUser(userDTO);
            return ResponseEntity.status(200).body(new TokenResponse(session.getAccessToken(), session.getRefreshToken()));
        } catch (Exception e){
            System.out.println("Ошибка при логине пользователя: " + e.getMessage());
            return ResponseEntity.status(400).body(
                    new TokenResponse(null, null, "Login error: " + e.getMessage())
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization") String authBearer){
        try{
            userService.logoutUser(authBearer);

            return ResponseEntity.status(200).body("Successful logout");
        } catch (Exception e){
            System.out.println("Ошибка при выходе из системы: " + e.getMessage());
            return ResponseEntity.status(400).body("Error logging out: " + e.getMessage());
        }
    }
}
