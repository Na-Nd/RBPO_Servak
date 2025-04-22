package ru.mtuci.rbposervak.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mtuci.rbposervak.entities.ENUMS.ROLE;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@JsonIgnoreProperties({"licenses", "devices"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Логин не может быть пустым")
    @Column(name = "login")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ROLE role;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<License> licenses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Device> devices;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LicenseHistory> licenseHistories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSession> sessions;

    @OneToMany(mappedBy = "changedByUser", cascade = CascadeType.ALL)
    private List<SignatureAudit> signatureAudits;

    public User(String login, String passwordHash, String email, ROLE role, List<License> licenses, List<Device> devices, List<LicenseHistory> licenseHistories) {
        this.login = login;
        this.password = passwordHash;
        this.email = email;
        this.role = role;
        this.licenses = licenses;
        this.devices = devices;
        this.licenseHistories = licenseHistories;
    }

    public User(String login, String passwordHash, String email, ROLE role, List<License> licenses, List<Device> devices) {
        this.login = login;
        this.password = passwordHash;
        this.email = email;
        this.role = role;
        this.licenses = licenses;
        this.devices = devices;
    }

    public User(String login, String passwordHash, String email, ROLE role, List<License> licenses) {
        this.login = login;
        this.password = passwordHash;
        this.email = email;
        this.role = role;
        this.licenses = licenses;
    }

    public User(String login, String passwordHash, String email) {
        this.login = login;
        this.password = passwordHash;
        this.email = email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isBlocked; // Аккаунт не заблокирован, если isBlocked == false
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }
}