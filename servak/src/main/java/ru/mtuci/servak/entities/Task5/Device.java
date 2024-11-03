package ru.mtuci.servak.entities.Task5;

import jakarta.persistence.*;
import ru.mtuci.servak.entities.User;

@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String macAddress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}