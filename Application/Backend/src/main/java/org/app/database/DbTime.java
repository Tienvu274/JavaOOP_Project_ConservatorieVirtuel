package org.app.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class DbTime {

    public static final ZoneId ZONE = ZoneId.of("Europe/Paris");

    private DbTime() {
    }

    public static Long toEpochSeconds(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        if (ZONE.getRules().getValidOffsets(value).size() != 1) {
            throw new IllegalArgumentException("Ambiguous/nonexistent Paris local time");
        }
        return value.atZone(ZONE).toEpochSecond();
    }

    public static LocalDateTime fromEpochSeconds(Long value) {
        if (value == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(value), ZONE);
    }

    public static LocalDateTime read(ResultSet row, String column) throws SQLException {
        long value = row.getLong(column);
        if (row.wasNull()) {
            return null;
        }
        return fromEpochSeconds(value);
    }
}