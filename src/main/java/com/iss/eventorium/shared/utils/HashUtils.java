package com.iss.eventorium.shared.utils;

import java.util.UUID;

public class HashUtils {

    public static String generateHash() {
        return UUID.randomUUID().toString();
    }
}