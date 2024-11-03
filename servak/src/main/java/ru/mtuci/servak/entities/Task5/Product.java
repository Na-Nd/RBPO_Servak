package ru.mtuci.servak.entities.Task5;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Boolean isBlocked;

    @OneToMany(mappedBy = "product")
    private List<License> licenses;

}