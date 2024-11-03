package ru.mtuci.servak.entities.Task5;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private LicenseType type;

    @Temporal(TemporalType.DATE)
    private Date firstActivationDate;

    @Temporal(TemporalType.DATE)
    private Date endingDate;

    private Boolean blocked;
    private Integer deviceCount;
    private Long ownerId;
    private Integer duration;
    private String description;

}