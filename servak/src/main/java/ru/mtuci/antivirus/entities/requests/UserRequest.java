package ru.mtuci.antivirus.entities.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String login;

    private String passwordHash;

    private String email;
}
