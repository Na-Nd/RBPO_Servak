package ru.nand.rbpo2.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "license")
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "code")
    private String licenseCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public License(int id, String licenseCode) {
        this.id = id;
        this.licenseCode = licenseCode;
    }

    public License(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public License() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
