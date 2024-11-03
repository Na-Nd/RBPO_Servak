package ru.mtuci.servak.entities;

import java.util.Date;

public class Ticket {

    private Date currentDate;

    private int livingTime;

    private Date activationDate;

    private Date expirationDate;

    private int userId;

    private int deviceId;

    private Boolean isBlocked;

    private String signature;

    public Ticket(Date currentDate, int livingTime, Date activationDate, Date expirationDate, int userId, int deviceId, Boolean isBlocked, String signature) {
        this.currentDate = currentDate;
        this.livingTime = livingTime;
        this.activationDate = activationDate;
        this.expirationDate = expirationDate;
        this.userId = userId;
        this.deviceId = deviceId;
        this.isBlocked = isBlocked;
        this.signature = signature;
    }

    public Ticket() {
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public int getLivingTime() {
        return livingTime;
    }

    public void setLivingTime(int livingTime) {
        this.livingTime = livingTime;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}

