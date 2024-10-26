package ru.nand.rbpo2.seriveces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nand.rbpo2.entities.License;
import ru.nand.rbpo2.entities.User;
import ru.nand.rbpo2.repositories.LicenseRepository;
import ru.nand.rbpo2.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final LicenseRepository licenseRepository;

    @Autowired
    public UserService(UserRepository userRepository, LicenseRepository licenseRepository) {
        this.userRepository = userRepository;
        this.licenseRepository = licenseRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public void addLicenseForUser(int userId, int licenseId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<License> licenseOptional = licenseRepository.findById(licenseId);

        if (userOptional.isPresent() && licenseOptional.isPresent()) {
            User user = userOptional.get();
            License license = licenseOptional.get();

            license.setUser(user);

            licenseRepository.save(license);
        } else {
            throw new IllegalArgumentException("User or License not found");
        }
    }

    public void deleteById(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
