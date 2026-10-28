package org.app.view;

import org.app.database.DbInitializer;

import java.util.List;

public class ConsoleSupport {
    public static boolean initializeDatabase() {
        try {
            DbInitializer.initialize();
            return true;
        } catch (Exception error) {
            System.out.println("[DB] Initialization failed: " + error.getMessage());
            System.out.println("Check database configuration and schema, " + "then restart the application.");
            return false;
        }
    }

    public static void show(List<?> values) {
        if (values.isEmpty()) {
            System.out.println("No records.");
            return;
        }
        for (Object value : values) {
            System.out.println(value);
        }
    }
}
