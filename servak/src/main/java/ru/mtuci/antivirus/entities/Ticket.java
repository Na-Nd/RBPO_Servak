package ru.mtuci.antivirus.entities;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    private Date currentDate;
    private int lifetime;
    private Date activationDate;
    private Date expirationDate;
    private Long userId;
    private Long deviceId;
    private Boolean isBlocked;
    private String signature;

    @Override
    public String toString() {
        return "Ticket{" +
                "currentDate=" + currentDate +
                ", lifetime=" + lifetime +
                ", activationDate=" + activationDate +
                ", expirationDate=" + expirationDate +
                ", userId=" + userId +
                ", deviceId=" + deviceId +
                ", isBlocked=" + isBlocked +
                ", signature='" + signature + '\'' +
                '}';
    }

    public String getBodyForSigning(){
        return String.format("Ticket:" +
                        "Current date: %s" +
                        "Lifetime: %d" +
                        "Activation date: %s" +
                        "Expiration date: %s" +
                        "User ID: %d" +
                        "Device ID: %d" +
                        "Is blocked: %b" +
                        "My mega secret string for signing XD",
                this.getCurrentDate(),
                this.getLifetime(),
                this.getActivationDate(),
                this.getExpirationDate(),
                this.getUserId(),
                this.getDeviceId(),
                this.getIsBlocked());
    }
}