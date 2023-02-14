package com.example.ogma.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {
    private final Map<String, String> data = new HashMap<>();

    public LoggedInUser(String uid, String name, String lastName, String middleName, String role, String email, String phone, String vk, String tg, String birthday) {
        this.data.put("UID", uid);
        this.data.put("name", name);
        this.data.put("lastName", lastName);
        this.data.put("middleName", middleName);
        this.data.put("role", role);
        this.data.put("email", email);
        this.data.put("vk", vk);
        this.data.put("tg", tg);
        this.data.put("phone", phone);
        this.data.put("birthday", birthday);
    }

    public Map<String, String> getData() { return data; }
}