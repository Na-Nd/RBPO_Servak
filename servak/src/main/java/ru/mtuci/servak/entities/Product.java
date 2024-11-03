package ru.mtuci.servak.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "is_blocked")
    private Boolean idBlocked;

    @OneToOne(mappedBy = "product")
    private License license;

    public Product(String name, Boolean idBlocked, License license) {
        this.name = name;
        this.idBlocked = idBlocked;
        this.license = license;
    }

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIdBlocked() {
        return idBlocked;
    }

    public void setIdBlocked(Boolean idBlocked) {
        this.idBlocked = idBlocked;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }
}
