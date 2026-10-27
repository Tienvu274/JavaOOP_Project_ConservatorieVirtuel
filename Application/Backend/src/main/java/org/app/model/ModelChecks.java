package org.app.model;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

final class ModelChecks {
    private ModelChecks() {
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static String text(String value, String field) {
        require(
                value != null && !value.trim().isEmpty(),
                field + " is required"
        );
        return value.trim();
    }

    public static String id(String value) {
        value = text(value, "ID");
        require(
                value.matches("[A-Za-z0-9_-]{1,40}"),
                "Invalid ID"
        );
        return value;
    }

    public static String type(String value) {
        value = text(value, "Instrument type")
                .toUpperCase(Locale.ROOT);
        require(
                value.equals("PIANO")
                        || value.equals("GUITAR")
                        || value.equals("VIOLIN")
                        || value.equals("FLUTE")
                        || value.equals("DRUMS"),
                "Unknown instrument type"
        );
        return value;
    }

    public static long price(long value) {
        require(value >= 0, "Price cannot be negative");
        return value;
    }

    public static LocalDateTime time(LocalDateTime value) {
        return Objects.requireNonNull(
                value,
                "Time is required"
        ).withNano(0);
    }
}
