package ru.mtuci.servak.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
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
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
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

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceLicense> deviceLicenses;

}
