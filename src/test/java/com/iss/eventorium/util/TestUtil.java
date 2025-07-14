package com.iss.eventorium.util;

import org.springframework.jdbc.core.JdbcTemplate;

public class TestUtil {

    public static final String ORGANIZER_EMAIL = "organizer@gmail.com";
    public static final String PASSWORD = "pera";
    public static final Long EVENT_WITH_BUDGET = 1L;
    public static final Long EVENT_WITHOUT_AGENDA_1 = 1L;
    public static final Long EVENT_WITHOUT_AGENDA_2 = 7L;
    public static final Long EVENT_WITH_AGENDA = 6L;
    public static final Long EVENT_WITH_GUESTS = 6L;
    public static final Long EVENT_WITHOUT_GUESTS = 1L;
    public static final Long EVENT_NOT_IN_DRAFT_STATE = 5L;
    public static final Long FORBIDDEN_EVENT_ID = 2L;
    public static final Long INVALID_EVENT = 500L;
    public static final Long INVALID_PRODUCT = 500L;

    private static void resetTable(JdbcTemplate jdbcTemplate, String tableName) {
        jdbcTemplate.execute(String.format("DELETE FROM %s;", tableName));
        jdbcTemplate.execute(String.format("ALTER TABLE %s ALTER COLUMN id RESTART WITH 1;", tableName));
    }

    public static void resetTables(JdbcTemplate jdbcTemplate) {
        resetTable(jdbcTemplate, "roles");
        resetTable(jdbcTemplate, "cities");
        resetTable(jdbcTemplate, "users");
        resetTable(jdbcTemplate, "event_types");
        resetTable(jdbcTemplate, "events");
    }

}
