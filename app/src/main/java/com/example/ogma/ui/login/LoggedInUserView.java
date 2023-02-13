package com.example.ogma.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private final String displayName, lastName, middleName, UID, email, vk, tg, phone, birthday, role;

    LoggedInUserView(String uid, String displayName, String lastName, String middleName, String role, String email, String phone, String vk, String tg, String birthday) {
        this.displayName = displayName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.UID = uid;
        this.email = email;
        this.role = role;
        this.vk = vk;
        this.tg = tg;
        this.phone = phone;
        this.birthday = birthday;
    }

    String getDisplayName() {
        return displayName;
    }
}