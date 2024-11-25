package ru.mtuci.servak.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
public class DeviceLicense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Переименовано с licenseId на id

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    @JsonBackReference
    private Device device;

    @ManyToOne
    @JoinColumn(name = "license_id", nullable = false) // Внешний ключ, указывающий на License
    private License license;

    @Temporal(TemporalType.DATE)
    private Date activationDate;
}
