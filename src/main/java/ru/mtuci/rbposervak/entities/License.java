package ru.mtuci.rbposervak.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "licenses")
@AllArgsConstructor
@NoArgsConstructor
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "type_id")
    @JsonBackReference
    private LicenseType type;

    @Column(name = "first_activation_date")
    private Date firstActivationDate;

    @Column(name = "ending_date")
    private Date endingDate;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @Column(name = "devices_count")
    private int devicesCount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private User owner;

    @Column(name = "duration")
    private int duration;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceLicense> deviceLicenses;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LicenseHistory> licenseHistories;

    public License(Long id, String code, User user, Product product, LicenseType type, Date firstActivationDate, Date endingDate, Boolean isBlocked, int devicesCount, User owner, int duration, String description) {
        this.id = id;
        this.code = code;
        this.user = user;
        this.product = product;
        this.type = type;
        this.firstActivationDate = firstActivationDate;
        this.endingDate = endingDate;
        this.isBlocked = isBlocked;
        this.devicesCount = devicesCount;
        this.owner = owner;
        this.duration = duration;
        this.description = description;
    }

    public License(String code, User user, Product product, LicenseType type, Date firstActivationDate, Date endingDate, Boolean isBlocked, int devicesCount, User owner, int duration, String description) {
        this.code = code;
        this.user = user;
        this.product = product;
        this.type = type;
        this.firstActivationDate = firstActivationDate;
        this.endingDate = endingDate;
        this.isBlocked = isBlocked;
        this.devicesCount = devicesCount;
        this.owner = owner;
        this.duration = duration;
        this.description = description;
    }

    @Override
    public String toString() {
        return "License{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", user=" + (user != null ? user.getId() : null) +
                ", productId=" + (product != null ? product.getId() : null) +
                ", type=" + (type != null ? type.getName() : null) +
                ", firstActivationDate=" + firstActivationDate +
                ", endingDate=" + endingDate +
                ", isBlocked=" + isBlocked +
                ", devicesCount=" + devicesCount +
                ", owner=" + (owner != null ? owner.getId() : null) +
                ", duration=" + duration +
                ", description='" + description + '\'' +
                '}';
    }

}
