package ru.mtuci.servak.entities.Task5;

import jakarta.persistence.*;
import ru.mtuci.servak.entities.License;

import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    private String passwordHash;
    private String email;
    private String role;

    @OneToMany(mappedBy = "user")
    private List<Device> devices;

    @OneToMany(mappedBy = "user")
    private List<License> licenses;

    @OneToMany(mappedBy = "user")
    private List<LicenseHistory> licenseHistories;

}