package ru.mtuci.servak.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "licenses")
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "license_code")
    private String code;
    @Column(name = "activation_date")
    private Date activationDate;
    @Column(name = "expiration_date")
    private Date expirationDate;
    @Column(name = "id_blocked")
    private Boolean isBlocked;
    @Column(name = "identifier")
    private String identifier;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    public License(String code, Date activationDate, Date expirationDate, Boolean isBlocked, String identifier, User user, Product product) {
        this.code = code;
        this.activationDate = activationDate;
        this.expirationDate = expirationDate;
        this.isBlocked = isBlocked;
        this.identifier = identifier;
        this.user = user;
        this.product = product;
    }

    public License() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
