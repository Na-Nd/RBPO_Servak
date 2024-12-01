package ru.mtuci.antivirus.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mac_address")
    private String macAddress;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DeviceLicense> deviceLicenses;

    public Device(String name, String macAddress, User user, List<DeviceLicense> deviceLicenses) {
        this.name = name;
        this.macAddress = macAddress;
        this.user = user;
        this.deviceLicenses = deviceLicenses;
    }

    public Device() {
    }

    public String getBody(){
        return String.format(
                "Device:\n" +
                "id: %d\n" +
                "name: %s\n" +
                "macAddress: %s\n" +
                "user: %s\n" +
                "deviceLicenses: %s\n",
                id, name, macAddress, user, deviceLicenses
        );
    }
}
