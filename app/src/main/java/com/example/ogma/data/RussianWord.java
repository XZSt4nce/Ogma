package com.example.ogma.data;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.annotation.NonNull;

public class RussianWord {
    public static String russian(@NonNull String str) {
        return new String(str.getBytes(ISO_8859_1), UTF_8);
    }
}
