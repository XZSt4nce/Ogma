package com.example.ogma.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {
    private final String UID;
    private final String displayName;
    private final String lastName;
    private final String middleName;
    private final String role;
    private final String email;
    private final String phone;
    private final String vk;
    private final String tg;
    private final String birthday;

    public LoggedInUser(String uid, String displayName, String lastName, String middleName, String role, String email, String phone, String vk, String tg, String birthday) {
        this.UID = uid;
        this.displayName = displayName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.vk = vk;
        this.tg = tg;
        this.birthday = birthday;
    }

    public String getUID() {
        return UID;
    }
    public String getDisplayName() { return displayName; }
    public String getLastName() {
        return lastName;
    }
    public String getMiddleName() {
        return middleName;
    }
    public String getRole() {
        return role;
    }
    public String getEmail() { return email; }
    public String getPhone() {
        return phone;
    }
    public String getVk() {
        return vk;
    }
    public String getTg() {
        return tg;
    }
    public String getBirthday() {
        return birthday;
    }
}