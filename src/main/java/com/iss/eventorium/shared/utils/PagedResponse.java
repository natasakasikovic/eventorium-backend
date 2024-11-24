package com.iss.eventorium.shared.utils;

import java.util.List;

public class PagedResponse<T> {
    private List<T> content;
    private int totalPages;
    private int totalElements;
}
