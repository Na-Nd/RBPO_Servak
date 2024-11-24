package ru.mtuci.servak.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.servak.entities.DTO.UserLoginDTO;
import ru.mtuci.servak.entities.DTO.UserRegisterDTO;
import ru.mtuci.servak.entities.ENUMS.ROLE;
import ru.mtuci.servak.entities.User;
import ru.mtuci.servak.services.UserService;
import ru.mtuci.servak.utils.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegister(@Valid @RequestBody UserRegisterDTO userDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body("Ошибка валидации: " + bindingResult.getAllErrors());
        }

        User user = new User(userDTO.getLogin(), passwordEncoder.encode(userDTO.getPassword()), userDTO.getEmail(), ROLE.ROLE_USER,null, null, null);

        userService.save(user);

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok("Регистрация пройдена, JWT: " + token);
    }


    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserLoginDTO userDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body("Ошибка валидации: " + bindingResult.getAllErrors());
        }

        User user = userService.findByLogin(userDTO.getLogin());
        if(user == null){
            return ResponseEntity.notFound().build();
        }

        if(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())){
            String token = jwtUtil.generateToken(userService.loadUserByUsername(user.getUsername()));
            return ResponseEntity.ok("Логин пройден, JWT:" + token);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/test")
    public String test(){
        return "success";
    }

}
