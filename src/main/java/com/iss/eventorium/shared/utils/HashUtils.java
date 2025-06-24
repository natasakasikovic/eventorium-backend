package com.iss.eventorium.shared.utils;

import java.util.UUID;

public class HashUtils {

    private HashUtils() {}

    public static String generateHash() {
        return UUID.randomUUID().toString();
    }
}