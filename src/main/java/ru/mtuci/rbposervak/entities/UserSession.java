package ru.mtuci.rbposervak.entities;

import jakarta.persistence.*;
import lombok.*;
import ru.mtuci.rbposervak.entities.ENUMS.session.STATUS;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    private LocalDateTime accessTokenExpires;

    private LocalDateTime refreshTokenExpires;

    private LocalDateTime sessionCreationTime;

    private LocalDateTime lastActivityTime;

    @Enumerated(EnumType.STRING)
    private STATUS status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Version
    @Column(name = "version")
    private Long version;
}

