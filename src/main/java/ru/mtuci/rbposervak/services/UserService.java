package ru.mtuci.rbposervak.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mtuci.rbposervak.entities.DTO.UserLoginDTO;
import ru.mtuci.rbposervak.entities.DTO.UserRegisterDTO;
import ru.mtuci.rbposervak.entities.ENUMS.ROLE;
import ru.mtuci.rbposervak.entities.User;
import ru.mtuci.rbposervak.entities.UserSession;
import ru.mtuci.rbposervak.repositories.UserRepository;
import ru.mtuci.rbposervak.utils.JwtUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionService userSessionService;
    private final JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByLogin(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public User findUserByLogin(String login){
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public User getUserById(Long id){
        return userRepository.getUserById(id);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /// Регистрация пользователя
    public UserSession registerUser(UserRegisterDTO userDTO) {
        // Существует ли пользователь с такой почтой или именем
        if(userRepository.findUserByEmail(userDTO.getEmail()).isPresent()){
            throw new RuntimeException("Пользователь с такой почтой уже существует");
        }
        if (userRepository.findUserByLogin(userDTO.getLogin()).isPresent()){
            throw new RuntimeException("Пользователь с таким username уже существует");
        }

        User user = User.builder()
                .login(userDTO.getLogin())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .role(ROLE.ROLE_USER)
                .isBlocked(false)
                .build();

        // Сохраняем и создаем сессию
        userRepository.save(user);

        return userSessionService.createUserSession(user);
    }

    /// Логин
    public UserSession loginUser(UserLoginDTO userDTO){
        User user = userRepository.findUserByLogin(userDTO.getLogin())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверка пароля
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        // Если пользователь найден и пароль совпал
        return userSessionService.createUserSession(user);
    }

    /// Логаут
    public void logoutUser(String authBearer){
        try {
            userSessionService.deactivateSessionByAccessToken(jwtUtil.resolveToken(authBearer));
            System.out.println("Пользователь вышел из системы");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выходе из системы: " + e.getMessage());
        }
    }

}
