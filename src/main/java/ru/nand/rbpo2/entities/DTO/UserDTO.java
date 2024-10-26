package ru.nand.rbpo2.entities.DTO;

import ru.nand.rbpo2.entities.License;
import ru.nand.rbpo2.entities.enums.ROLE;

import java.util.List;

public class UserDTO {
    private int id;
    private String login;
    private ROLE role;
    private List<License> licenses;

    public UserDTO(int id, String login, ROLE role, List<License> licenses) {
        this.id = id;
        this.login = login;
        this.role = role;
        this.licenses = licenses;
    }

    public List<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }
}
