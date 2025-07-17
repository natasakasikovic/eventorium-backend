package com.iss.eventorium.util;

import org.springframework.jdbc.core.JdbcTemplate;

public class TestUtil {

    public static final String ORGANIZER_EMAIL = "organizer@gmail.com";
    public static final String ORGANIZER_EMAIL_2 = "organizer2@gmail.com";
    public static final String PASSWORD = "pera";
    public static final Long EXISTING_EVENT = 1L;
    public static final Long INVALID_EVENT = 0L;
    public static final Long INVALID_PRODUCT = 0L;
    public static final Long NEW_BUDGET_ITEM = 4L;
    public static final Long PURCHASED_PRODUCT = 1L;
    public static final Long NOT_PROCESSED_PRODUCT = 5L;
    public static final Long DELETED_PRODUCT = 6L;
    public static final Long INVISIBLE_PRODUCT = 7L;
    public static final Long UNAVAILABLE_PRODUCT = 8L;
    public static final Long EVENT_WITHOUT_AGENDA_1 = 1L;
    public static final Long EVENT_WITHOUT_AGENDA_2 = 7L;
    public static final Long EVENT_WITH_AGENDA = 6L;
    public static final Long EVENT_WITH_GUESTS = 6L;
    public static final Long EVENT_WITHOUT_GUESTS = 1L;
    public static final Long EVENT_NOT_IN_DRAFT_STATE = 5L;
    public static final Long FORBIDDEN_EVENT_ID = 2L;
    public static final Long NON_EXISTING_ENTITY_ID = 0L;
    public static final Long PLANNED_BUDGET_ITEM = 8L;
    public static final Long ORGANIZER_2_EVENT = 2L;
    public static final Long PROCESSED_BUDGET_ITEM = 1L;
    public static final Long PLANNED_BUDGET_ITEM_2 = 9L;
    public static final Long ORGANIZER_2_BUDGET_ITEM = 3L;

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
