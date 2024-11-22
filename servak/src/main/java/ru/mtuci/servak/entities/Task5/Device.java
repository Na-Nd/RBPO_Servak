package ru.mtuci.servak.entities.Task5;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mtuci.servak.entities.User;

import java.util.List;

@Setter
@Getter
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String macAddress;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceLicense> deviceLicenses;

}
