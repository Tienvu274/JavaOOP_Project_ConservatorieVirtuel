package org.app.database;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DbContext {
    private static final Properties FILE_SETTINGS = loadSettings();

    private DbContext() {
    }

    private static Properties loadSettings() {
        Properties settings = new Properties();
        Path file = Path.of(".env");
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                settings.load(reader);
            } catch (IOException error) {
                throw new IllegalStateException("Cannot read .env file: " + file.toAbsolutePath(), error);
            }
        }
        return settings;
    }

    private static String setting(String name, String defaultValue
    ) {
        String environmentValue = System.getenv(name);
        if (environmentValue != null) {
            return environmentValue;
        }
        return FILE_SETTINGS.getProperty(name, defaultValue);
    }

    public static String getDatabaseName() {
        String name = setting("DB_NAME", "conservatoire_oop");
        if (!name.matches("[A-Za-z0-9_]{1,64}")) {
            throw new IllegalArgumentException("Invalid DB_NAME");
        }
        return name;
    }

    private static String address() {
        String host = setting("DB_HOST", "localhost");
        int port = Integer.parseInt(setting("DB_PORT", "3306"));
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Invalid DB_PORT");
        }
        return "jdbc:mysql://" + host + ":" + port + "/";
    }

    private static String options() {
        return "?connectionTimeZone=UTC";
    }

    private static Connection connect(String url) throws SQLException {
        String user = setting("DB_USER", "root");
        String password = setting("DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }

    public static Connection openServer() throws SQLException {
        return connect(address() + options());
    }

    public static Connection open()throws SQLException {
        return connect(address()
                + getDatabaseName()
                + options()
        );
    }
}