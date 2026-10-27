package org.app.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DbInitializer {

    private DbInitializer() {
    }

    private static final String[] COLUMNS = {
            "student:id,first_name,last_name,address,birth_date,phone,email,level",
            "teacher:id,first_name,last_name,hourly_rate_cents,qualifications",
            "student_preference:student_id,instrument_type",
            "teacher_specialization:teacher_id,instrument_type",
            "teacher_availability:teacher_id,slot_number,starts_at,ends_at",
            "room:id,name,capacity,hourly_price_cents",
            "instrument:id,name,instrument_type,hourly_price_cents",
            "package_offer:id,name,price_cents,unlimited,lesson_count",
            "enrollment:id,student_id,package_id,valid_from,valid_through,purchased_at,package_name,price_cents,unlimited,lesson_count",
            "activity:id,kind,starts_at,duration_minutes,room_id,teacher_id,status,changed_at,instrument_type,group_lesson,exam_name,exam_capacity,renter_id,rental_price_cents",
            "activity_instrument:activity_id,instrument_id",
            "lesson_participation:lesson_id,student_id,enrollment_id,minutes,state,reserved_at,settled_at",
            "exam_registration:exam_id,student_id,registered_at",
            "exam_result:exam_id,student_id,passed,score,recorded_at",
            "charge:id,student_id,source_type,enrollment_id,activity_id,description,amount_cents,created_at",
            "payment:id,student_id,amount_cents,method,paid_at"
    };

    private static final Pattern INDEX = Pattern.compile("(?i)^CREATE\\s+INDEX\\s+(\\w+)\\s+ON\\s+(\\w+)\\s*\\(");

    public static String[] getBusinessTables() {
        String[] result = new String[COLUMNS.length];
        for (int i = 0; i < COLUMNS.length; i++) {
            result[i] = COLUMNS[i].split(":")[0];
        }
        return result;
    }

    public static void initialize() throws SQLException, IOException {
        String name = DbContext.getDatabaseName();
        try (Connection server = DbContext.openServer(); Statement statement = server.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + name + " CHARACTER SET utf8mb4 " + "COLLATE utf8mb4_0900_as_cs");
        }
        try (Connection connection = DbContext.open()) {
            runSchema(connection);
            verifyColumns(connection);
            ensureState(connection);
            if (seedCompleted(connection)) {
                System.out.println("[DB] Tables ready; seed already processed. " + "Existing data retained.");
                return;
            }
            if (containsBusinessData(connection)) {
                markCompleted(connection);
                System.out.println("[DB] Existing business data found; " + "automatic seed skipped.");
                return;
            }
            connection.setAutoCommit(false);
            try {
                for (String sql : readSql("/sql/seed.sql")) {
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate(sql);
                    }
                }
                markCompleted(connection);
                connection.commit();
                System.out.println("[DB] 16 business tables ready; " + "sample data inserted once.");
            } catch (SQLException | IOException error) {
                connection.rollback();
                throw error;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private static void ensureState(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS __bootstrap_state "
                            + "(id INT PRIMARY KEY, "
                            + "completed BOOLEAN NOT NULL) "
                            + "ENGINE=InnoDB"
            );
            statement.executeUpdate(
                    "INSERT INTO __bootstrap_state(id, completed) "
                            + "VALUES(1,FALSE) "
                            + "ON DUPLICATE KEY UPDATE id=id"
            );
        }
    }

    private static boolean seedCompleted(Connection connection) throws SQLException {
        try (
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(
                        "SELECT completed "
                                + "FROM __bootstrap_state "
                                + "WHERE id=1"
                )) {
            return result.next() && result.getBoolean(1);
        }
    }

    private static void markCompleted(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "UPDATE __bootstrap_state "
                            + "SET completed=TRUE "
                            + "WHERE id=1"
            );
        }
    }

    private static boolean containsBusinessData(Connection connection) throws SQLException {
        for (String table : getBusinessTables()) {
            try (Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery("SELECT 1 FROM " + table + " LIMIT 1")) {
                if (result.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void runSchema(Connection connection) throws SQLException, IOException {
        for (String original : readSql("/sql/schema.sql")) {
            String sql = original.trim();
            String upper = sql.toUpperCase(Locale.ROOT);

            if (upper.startsWith("CREATE DATABASE")
                    || upper.startsWith("USE ")) {
                continue;
            }

            if (upper.startsWith("CREATE TABLE ")) {
                sql = "CREATE TABLE IF NOT EXISTS "
                        + sql.substring("CREATE TABLE ".length());
            }
            Matcher match = INDEX.matcher(sql);
            if (match.find()
                    && indexExists(
                    connection,
                    match.group(2),
                    match.group(1)
            )) {
                continue;
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

    private static boolean indexExists(Connection connection, String table, String index) throws SQLException {
        try (
                ResultSet result = connection.getMetaData().getIndexInfo(connection.getCatalog(), null, table, false, false)) {
            while (result.next()) {
                if (index.equals(result.getString("INDEX_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void verifyColumns(Connection connection) throws SQLException {
        for (String definition : COLUMNS) {
            String[] parts = definition.split(":");
            List<String> actual = new ArrayList<>();
            try (
                    ResultSet result =
                            connection.getMetaData().getColumns(
                                    connection.getCatalog(),
                                    null,
                                    parts[0],
                                    "%"
                            )
            ) {
                while (result.next()) {
                    actual.add(
                            result.getString("COLUMN_NAME")
                    );
                }
            }
            for (String column : parts[1].split(",")) {
                if (!actual.contains(column)) {
                    throw new SQLException(
                            "Existing table "
                                    + parts[0]
                                    + " lacks column "
                                    + column
                                    + ". Compare schema.sql; "
                                    + "this initializer does not alter existing columns."
                    );
                }
            }
        }
    }

    private static List<String> readSql(String resource) throws IOException {
        InputStream input = DbInitializer.class.getResourceAsStream(resource);
        if (input == null) {
            throw new IOException("Missing SQL resource: " + resource);
        }
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.stripLeading().startsWith("--")
                        && !line.stripLeading().startsWith("#")) {
                    text.append(line).append('\n');
                }
            }
        }
        List<String> result = new ArrayList<>();
        for (String sql : text.toString().split(";")) {
            if (!sql.isBlank()) {
                result.add(sql.trim());
            }
        }
        return result;
    }
}