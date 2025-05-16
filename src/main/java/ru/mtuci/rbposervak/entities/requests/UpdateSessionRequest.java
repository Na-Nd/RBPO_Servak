package ru.mtuci.rbposervak.entities.requests;

import lombok.Data;
import ru.mtuci.rbposervak.entities.ENUMS.session.STATUS;

import java.time.LocalDateTime;

@Data
public class UpdateSessionRequest {
    private String accessToken;

    private String refreshToken;

    private LocalDateTime accessTokenExpires;

    private LocalDateTime refreshTokenExpires;

    private LocalDateTime sessionCreationTime;

    private LocalDateTime lastActivityTime;

    private STATUS status;

    private Long version;
}
