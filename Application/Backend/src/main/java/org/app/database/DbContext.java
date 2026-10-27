package org.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbContext {
    private static final String URL = "jdbc:mysql://localhost:3306/conservatoire_oop?connectionTimeZone=UTC";

    private DbContext() { }

    public static Connection open() throws SQLException {
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");
        return DriverManager.getConnection(URL, user, password);
    }
}