package ru.mtuci.servak.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

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
    @JsonBackReference  // Предотвращает рекурсию с обратной стороны
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference  // Обратная сторона отношений
    private Product product;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    @JsonBackReference  // Обратная сторона отношений
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

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LicenseHistory> licenseHistories;

    public License(Product product, User owner, LicenseType licenseType, String code, String description, Integer deviceCount, Integer duration) {
        this.product = product;
        this.user = owner;
        this.ownerId = owner.getId();
        this.type = licenseType;
        this.code = code;
        this.description = description;
        this.deviceCount = deviceCount;
        this.duration = duration;
    }

    public License() {

    }
}
