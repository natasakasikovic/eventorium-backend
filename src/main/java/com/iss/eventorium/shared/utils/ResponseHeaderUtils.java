package com.iss.eventorium.shared.utils;

import org.springframework.http.HttpHeaders;

public class ResponseHeaderUtils {

    private ResponseHeaderUtils() {}

    public static HttpHeaders createPdfHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        return headers;
    }
}
