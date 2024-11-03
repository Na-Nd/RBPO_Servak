package ru.mtuci.servak.entities.Task5;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class DeviceLicense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long licenseId;

    private Long deviceId;

    @Temporal(TemporalType.DATE)
    private Date activationDate;
}
