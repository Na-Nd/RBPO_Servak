package ru.mtuci.antivirus.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mtuci.antivirus.entities.User;
import ru.mtuci.antivirus.repositories.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String login, String password) {
        User user = userRepository.findUserByLogin(login);
        if(user != null){
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}
