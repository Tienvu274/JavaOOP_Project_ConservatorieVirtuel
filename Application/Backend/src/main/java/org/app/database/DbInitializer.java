package org.app.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class databaseInitializer {
    private static final String DB_NAME = "conservatoire_oop";
    private static final String SERVER_URL =
            "jdbc:mysql://localhost:3306/?connectionTimeZone=UTC";
    private static final String DATABASE_URL =
            "jdbc:mysql://localhost:3306/" + DB_NAME + "?connectionTimeZone=UTC";
    private static final Pattern INDEX_PATTERN = Pattern.compile(
            "(?i)^CREATE\\s+INDEX\\s+(\\w+)\\s+ON\\s+(\\w+)\\s*\\(");

    private databaseInitializer() {}

    public static void initialize() {
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            boolean alreadyExists;
            try (Connection server = DriverManager.getConnection(SERVER_URL, user, password)) {
                alreadyExists = databaseExists(server);
                if (!alreadyExists) {
                    try (Statement sql = server.createStatement()) {
                        sql.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME
                                + " CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs");
                    }
                    System.out.println("[DB] New database created: " + DB_NAME);
                }
            }

            try (Connection db = DriverManager.getConnection(DATABASE_URL, user, password)) {
                // For manually existing databases, perform no writes (requested policy).
                if (alreadyExists && !tableExists(db, "__bootstrap_state")) {
                    System.out.println("[DB] Database already existed: schema and data unchanged.");
                    return;
                }

                ensureBootstrapState(db);
                if (bootstrapFinished(db)) {
                    System.out.println("[DB] Database already initialized: nothing changed.");
                    return;
                }

                runSchema(db);

                db.setAutoCommit(false);
                try {
                    for (String sql : sqlStatements("/sql/seed.sql")) {
                        try (Statement s = db.createStatement()) {
                            s.execute(sql);
                        }
                    }
                    try (Statement s = db.createStatement()) {
                        s.executeUpdate("UPDATE __bootstrap_state SET completed = TRUE WHERE id = 1");
                    }
                    db.commit();
                    System.out.println("[DB] 16 business tables created; demo data inserted.");
                } catch (SQLException | IOException failure) {
                    db.rollback();
                    throw failure;
                } finally {
                    db.setAutoCommit(true);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "MySQL Connector/J is missing. Reload Maven dependencies.", e);
        } catch (SQLException | IOException e) {
            throw new IllegalStateException(
                    "Database bootstrap failed. Check that MySQL is running, "
                            + "DB_USER/DB_PASSWORD, and MySQL 8.0.16+.", e);
        }
    }

    private static boolean databaseExists(Connection server) throws SQLException {
        String query = "SELECT COUNT(*) FROM information_schema.SCHEMATA "
                + "WHERE SCHEMA_NAME = ?";
        try (PreparedStatement ps = server.prepareStatement(query)) {
            ps.setString(1, DB_NAME);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private static boolean tableExists(Connection db, String table) throws SQLException {
        String query = "SELECT COUNT(*) FROM information_schema.TABLES "
                + "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setString(1, DB_NAME);
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private static void ensureBootstrapState(Connection db) throws SQLException {
        try (Statement s = db.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS __bootstrap_state "
                    + "(id INT PRIMARY KEY, completed BOOLEAN NOT NULL) ENGINE=InnoDB");
            s.executeUpdate("INSERT INTO __bootstrap_state(id,completed) VALUES(1,FALSE) "
                    + "ON DUPLICATE KEY UPDATE id = id");
        }
    }

    private static boolean bootstrapFinished(Connection db) throws SQLException {
        try (Statement s = db.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT completed FROM __bootstrap_state WHERE id = 1")) {
            return rs.next() && rs.getBoolean(1);
        }
    }

    private static void runSchema(Connection db) throws SQLException, IOException {
        for (String raw : sqlStatements("/sql/schema.sql")) {
            String sql = raw.trim();
            String upper = sql.toUpperCase(java.util.Locale.ROOT);
            if (upper.startsWith("CREATE DATABASE") || upper.startsWith("USE ")) {
                continue;
            }
            if (upper.startsWith("CREATE TABLE ")) {
                sql = sql.replaceFirst("(?i)^CREATE\\s+TABLE\\s+",
                        "CREATE TABLE IF NOT EXISTS ");
            }
            Matcher m = INDEX_PATTERN.matcher(sql);
            if (m.find() && indexExists(db, m.group(2), m.group(1))) {
                continue;
            }
            try (Statement s = db.createStatement()) {
                s.execute(sql);
            }
        }
    }

    private static boolean indexExists(Connection db, String table, String index)
            throws SQLException {
        String query = "SELECT COUNT(*) FROM information_schema.STATISTICS "
                + "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND INDEX_NAME = ?";
        try (PreparedStatement ps = db.prepareStatement(query)) {
            ps.setString(1, DB_NAME);
            ps.setString(2, table);
            ps.setString(3, index);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private static List<String> sqlStatements(String resource) throws IOException {
        InputStream input = databaseInitializer.class.getResourceAsStream(resource);
        if (input == null) {
            throw new IOException("SQL resource missing: " + resource);
        }
        StringBuilder cleaned = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (!line.stripLeading().startsWith("--") && !line.stripLeading().startsWith("#")) {
                    cleaned.append(line).append('\n');
                }
            }
        }
        List<String> result = new ArrayList<>();
        for (String sql : cleaned.toString().split(";")) {
            if (!sql.isBlank()) result.add(sql.trim());
        }
        return result;
    }
}
