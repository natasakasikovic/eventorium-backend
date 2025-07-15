package com.iss.eventorium.util;

import org.springframework.jdbc.core.JdbcTemplate;

public class TestUtil {

    public static final String RESERVATION_ENDPOINT = "/api/v1/events/{event-id}/services/{service-id}/reservation";

    public static final String ORGANIZER_EMAIL = "organizer@gmail.com";
    public static final String PASSWORD = "pera";

    public static final Long NON_EXISTENT_ENTITY_ID = 0L;

    public static final Long EVENT_WITH_BUDGET = 1L;
    public static final Long EVENT_WITHOUT_AGENDA_1 = 1L;
    public static final Long EVENT_WITHOUT_AGENDA_2 = 7L;
    public static final Long EVENT_WITH_AGENDA = 6L;
    public static final Long EVENT_WITH_GUESTS = 6L;
    public static final Long EVENT_WITHOUT_GUESTS = 1L;
    public static final Long EVENT_NOT_IN_DRAFT_STATE = 5L;
    public static final Long FORBIDDEN_EVENT_ID = 2L;
    public static final Long INVALID_EVENT = 500L;
    public static final Long EVENT_ID_NOT_OWNED_BY_LOGGED_IN_ORGANIZER = 3L;
    public static final Long VALID_EVENT_ID_FOR_RESERVATION = 8L;

    public static final Long INVALID_PRODUCT = 500L;

    public static final Long UNAVAILABLE_SERVICE_ID = 8L;
    public static final Long SERVICE_ID_RESERVATION_DEADLINE_EXPIRED = 9L; // // Refers to a VALID_EVENT_ID_FOR_RESERVATION
    public static final Long RESERVABLE_SERVICE_ID = 5L; // TODO: vidi koji ces

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
