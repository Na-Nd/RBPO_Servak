package ru.mtuci.antivirus.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Ticket {

    private Date currentDate;
    private int lifetime;
    private Date activationDate;
    private Date expirationDate;
    private Long userId;
    private Long deviceId;
    private Boolean isBlocked;
    private String signature;

    public Ticket(Date currentDate, int lifetime, Date activationDate, Date expirationDate, Long userId, Long deviceId, Boolean isBlocked, String signature) {
        this.currentDate = currentDate;
        this.lifetime = lifetime;
        this.activationDate = activationDate;
        this.expirationDate = expirationDate;
        this.userId = userId;
        this.deviceId = deviceId;
        this.isBlocked = isBlocked;
        this.signature = signature;
    }

    public Ticket() {
    }

    public Ticket(Date currentDate, int lifetime, Date activationDate, Date expirationDate, Long userId, Long deviceId, Boolean isBlocked) {
        this.currentDate = currentDate;
        this.lifetime = lifetime;
        this.activationDate = activationDate;
        this.expirationDate = expirationDate;
        this.userId = userId;
        this.deviceId = deviceId;
        this.isBlocked = isBlocked;
    }

    public String getBody(){
        return String.format("Ticket:\n" +
                "Current date: %s\n" +
                "Lifetime: %d\n" +
                "Activation date: %s\n" +
                "Expiration date: %s\n" +
                "User ID: %d\n" +
                "Device ID: %d\n" +
                "Is blocked: %b\n" +
                "Signature: %s\n",
                this.getCurrentDate(),
                this.getLifetime(),
                this.getActivationDate(),
                this.getExpirationDate(),
                this.getUserId(),
                this.getDeviceId(),
                this.getIsBlocked(),
                this.getSignature());
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