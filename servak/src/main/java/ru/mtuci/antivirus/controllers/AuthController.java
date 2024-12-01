package ru.mtuci.antivirus.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.antivirus.entities.DTO.UserLoginDTO;
import ru.mtuci.antivirus.entities.DTO.UserRegisterDTO;
import ru.mtuci.antivirus.entities.ENUMS.ROLE;
import ru.mtuci.antivirus.entities.User;
import ru.mtuci.antivirus.services.UserService;
import ru.mtuci.antivirus.utils.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/test")
    public String test() {
        return "Tested successfully";
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserRegisterDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation error: " + bindingResult.getAllErrors());
        }

        // Check if user with this login already exists
        if(userService.findUserByLogin(userDTO.getLogin()) != null){
            return ResponseEntity.badRequest().body("User with this login already exists");
        }

        User user = new User(userDTO.getLogin(), passwordEncoder.encode(userDTO.getPassword()), userDTO.getEmail(), ROLE.ROLE_USER, null);
        userService.saveUser(user);

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok("Registration competed, JWT: " + token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserLoginDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation error: " + bindingResult.getAllErrors());
        }

        User user = userService.findUserByLogin(userDTO.getLogin());

        // If login not exist
        if(user == null){
            return ResponseEntity.badRequest().body("User not found");
        }

        // If password didnt match
        if(!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())){
            return ResponseEntity.status(401).build();
        }

        String token = jwtUtil.generateToken(userService.loadUserByUsername(user.getUsername()));
        return ResponseEntity.ok("Login completed, JWT: " + token);
    }
}
