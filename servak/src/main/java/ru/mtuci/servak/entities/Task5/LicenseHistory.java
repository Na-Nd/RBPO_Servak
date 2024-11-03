package ru.mtuci.servak.entities.Task5;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class LicenseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;

    @Temporal(TemporalType.DATE)
    private Date changeDate;

    private String description;

}
