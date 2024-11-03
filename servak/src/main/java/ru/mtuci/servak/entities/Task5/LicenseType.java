package ru.mtuci.servak.entities.Task5;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class LicenseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer defaultDuration;
    private String description;

    @OneToMany(mappedBy = "type")
    private List<License> licenses;

}
