package com.example.ogma.ui.login;

import java.util.HashMap;
import java.util.Map;

public class LoggedInUserView {
    public static final Map<String, String> data = new HashMap<>();

    public LoggedInUserView(String uid, String name, String lastName, String middleName, String role, String email, String phone, String vk, String tg, String birthday) {
        data.put("UID", uid);
        data.put("name", name);
        data.put("lastName", lastName);
        data.put("middleName", middleName);
        data.put("role", role);
        data.put("email", email);
        data.put("vk", vk);
        data.put("tg", tg);
        data.put("phone", phone);
        data.put("birthday", birthday);
    }
    public LoggedInUserView() { }

    public Map<String, String> getData() { return data; }
}