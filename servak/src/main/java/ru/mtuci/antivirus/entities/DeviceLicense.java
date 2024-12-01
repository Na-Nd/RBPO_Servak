package ru.mtuci.antivirus.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "device_licenses")
public class DeviceLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "license_id", nullable = false)
    @JsonBackReference
    private License license;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    @JsonBackReference
    private Device device;

    @Column(name = "activation_date")
    private Date activationDate;

    public DeviceLicense(License license, Device device, Date activationDate) {
        this.license = license;
        this.device = device;
        this.activationDate = activationDate;
    }

    public DeviceLicense() {
    }
}
