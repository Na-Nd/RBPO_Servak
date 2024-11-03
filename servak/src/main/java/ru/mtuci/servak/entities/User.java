package ru.mtuci.servak.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mtuci.servak.entities.ENUMS.ROLE;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Логин не может быть пустым")
    @Column(name = "login")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ROLE role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<License> licenses;

    public User(int id, String login, String password, ROLE role, List<License> licenses) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.licenses = licenses;
    }

    public User(String login, String password, ROLE role, List<License> licenses) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.licenses = licenses;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> role.name());
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public ROLE getRole() {
        return role;
    }

    public List<License> getLicenses() {
        return licenses;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }
}

